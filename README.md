# Form Management System

A RESTful API backend for a dynamic form management system built with Spring Boot 4.x.

---

## Table of Contents

- [System Design Decisions](#system-design-decisions)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Prerequisites](#prerequisites)
- [Running Locally (Without Docker)](#running-locally-without-docker)
- [Running with Docker](#running-with-docker)
- [Running Tests](#running-tests)
- [API Documentation (Swagger UI)](#api-documentation-swagger-ui)

---

## System Design Decisions

### 1. Entity Design — Why 5 tables instead of 1?

The core challenge of a "dynamic form" system is that field types and their validation rules change at runtime — you don't know at compile time what fields a form will contain.

**Decision:** Normalize the schema into 5 tables: `forms`, `fields`, `field_options`, `submissions`, `submission_values`.

- `forms` → `fields`: One-to-many. A form owns its fields with `CASCADE ALL` so deleting a form cleans up all its fields automatically.
- `fields` → `field_options`: An `@ElementCollection` mapped to a separate table. Only populated for `SELECT`-type fields, keeping the `fields` table lean.
- `submissions` → `submission_values`: Each submitted answer is a row. All values are stored as `TEXT`, with the actual type interpretation deferred to the `FieldValidatorEngine` at write-time. This trades some query complexity for maximum schema flexibility — you can add a new `FieldType` without a migration.

**Tradeoff acknowledged:** Storing everything as string means you lose SQL-level type constraints. This was an intentional choice to prioritize flexibility over strictness at the database layer.

### 2. Validation Architecture — The FieldValidatorEngine Strategy

**Decision:** All business validation logic is centralized in a single `FieldValidatorEngine` component, invoked by `SubmissionServiceImpl` at form-submit time. The controller layer only performs structural validation (via `@Valid` / Bean Validation annotations on DTOs).

This means validation is **two-layered**:
- **Layer 1 (Controller):** Structure — is the request body well-formed? Are required DTO fields present?
- **Layer 2 (Service):** Semantics — does the submitted value make sense given the `FieldType`? (e.g., is a DATE field value a real future date? Is a COLOR value a valid hex?)

This pattern keeps controllers thin and makes the validation engine independently testable without loading a Spring Context.

### 3. Status-Based Access Control

Forms have a `status` field (`active` | `draft`). Only `active` forms appear in the employee-facing `/api/forms/active` endpoint and can be submitted. Draft forms are invisible to employees.

This is a simple but effective soft-gate that the admin controls without needing a separate permission system.

### 4. Exception Handling — Global Handler Hierarchy

`GlobalExceptionHandler` handles exceptions in a specific priority order:
1. `IllegalArgumentException` → 400 Bad Request (business logic violations from `FieldValidatorEngine`)
2. `MethodArgumentNotValidException` → 400 Validation Error (DTO annotation failures)
3. `RuntimeException` → 404 Not Found (if message contains "không tìm thấy") or 400
4. `Exception` → 500 Internal Server Error (catch-all, hides stack traces from clients)

**Why RuntimeException for Not Found?** To avoid creating a custom `FormNotFoundException` hierarchy for this scope. The message-content-based routing is a pragmatic shortcut documented here intentionally.

### 5. Test Architecture

Three distinct test strategies are used, matching the three layers of the application:

| Layer | Strategy | Tool | Spring Context? |
|---|---|---|---|
| Validation | Unit Test | JUnit 5 only | ❌ None |
| Service | Unit Test | Mockito | ❌ None |
| Controller | Standalone MockMvc | Mockito + MockMvc | ❌ None (standaloneSetup) |
| Integration | Full Context | @SpringBootTest | ✅ Full (needs DB) |

> **Note on `@WebMvcTest`:** Spring Boot 4.x removed the MVC slice from `spring-boot-test-autoconfigure`. Controller tests therefore use `MockMvcBuilders.standaloneSetup(controller)` which achieves identical isolation without the slice annotation.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 4.1.0 | Framework |
| Spring Data JPA | (managed) | ORM / Repository layer |
| Hibernate | (managed) | JPA implementation |
| MariaDB / MySQL | 11 / 8+ | Primary database |
| Flyway | (managed) | Database schema migration |
| Lombok | (managed) | Boilerplate reduction |
| springdoc-openapi | 2.5.0 | Swagger UI / OpenAPI docs |
| JUnit 5 + Mockito | (managed) | Unit & Controller tests |

---

## Project Structure

Database schema script: [schema.sql](schema.sql)

```
src/
├── main/
│   ├── java/com/example/formmanagement/
│   │   ├── controller/          # REST Controllers (API layer)
│   │   ├── service/             # Service interfaces
│   │   │   └── impl/            # Business logic implementations
│   │   ├── entity/              # JPA Entities (DB tables)
│   │   ├── repository/          # Spring Data JPA repositories
│   │   ├── dto/                 # Request/Response DTOs
│   │   │   ├── form/
│   │   │   ├── field/
│   │   │   └── submission/
│   │   ├── exception/           # Global exception handler + ErrorResponse
│   │   └── validation/          # FieldValidatorEngine (dynamic validation)
│   └── resources/
│       ├── application.yml      # App configuration
│       └── db/migration/        # Flyway SQL migration scripts
└── test/
    └── java/com/example/formmanagement/
        ├── controller/          # Controller tests (MockMvc standalone)
        ├── service/impl/        # Service unit tests (Mockito)
        ├── validation/          # Validation unit tests (pure JUnit)
        └── integration/         # Integration tests (full context, needs DB)
```

---

## API Endpoints

### Form Management (Admin)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/forms` | Create a new form |
| `GET` | `/api/forms` | Get all forms |
| `GET` | `/api/forms/{id}` | Get a form by ID (includes fields) |
| `PUT` | `/api/forms/{id}` | Update a form |
| `DELETE` | `/api/forms/{id}` | Delete a form (cascades to fields) |

### Field Management

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/forms/{formId}/fields` | Add a field to a form |
| `GET` | `/api/fields` | Get all fields |
| `GET` | `/api/fields/{id}` | Get a field by ID |
| `PUT` | `/api/forms/{formId}/fields/{id}` | Update a field |
| `DELETE` | `/api/forms/{formId}/fields/{id}` | Delete a field |

### Submission (Employee)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/forms/active` | Get all active forms (for employees) |
| `POST` | `/api/forms/{id}/submit` | Submit answers to a form |
| `GET` | `/api/submissions` | Get all past submissions |

---

## Prerequisites

- **Java 17+** — [Download Adoptium](https://adoptium.net/)
- **MariaDB 10.6+ or MySQL 8+** — Running on port `3307` (or adjust `application.yml`)
- **Maven 3.9+** — or use the included `mvnw` wrapper

---

## Running Locally (Without Docker)

### Step 1 — Start MariaDB

Ensure MariaDB is running and accessible. The application will auto-create the database `form_management_db` on first start (via `createDatabaseIfNotExist=true`).

Default connection in `application.yml`:
```
Host: localhost
Port: 3307
Database: form_management_db
Username: root
Password: (empty)
```

To change these, edit `src/main/resources/application.yml`.

### Step 2 — Run the application

```bash
# Using the Maven wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The server starts at **http://localhost:8081**.

Flyway will automatically run `V1__Init_schema.sql` to create all tables on first startup.

---

## Running with Docker

Docker Compose starts both the MariaDB database and the Spring Boot application with a single command. No local Java or MariaDB installation required.

### Step 1 — Build and start

```bash
docker compose up --build
```

This will:
1. Build the application JAR inside a Maven container
2. Start MariaDB (`form_management_db`) on port `3307`
3. Wait for MariaDB to be healthy (via healthcheck)
4. Start the Spring Boot app on port `8081`

### Step 2 — Access the application

- **API Base URL:** http://localhost:8081
- **Swagger UI:** http://localhost:8081/swagger-ui/index.html

### Stop

```bash
docker compose down

# To also remove the database volume (resets all data):
docker compose down -v
```

## Git Commit & Push

After verifying the application locally, commit the project with a clear message and push to your Git repository:

```bash
git add .
git commit -m "Complete form-management backend with README, Docker, DB migration, and cleanup"
git push origin main
```

If your branch is different, replace `main` with the correct branch name.

---

## Running Tests

### Unit Tests only (no database required)

Runs all `*Test.java` files — controller, service, and validation tests. Completes in seconds.

```bash
./mvnw test

# Windows
mvnw.cmd test
```

### Integration Tests (requires running MariaDB)

Runs both unit tests and `*IT.java` integration tests that start the full Spring context and connect to a real database.

```bash
./mvnw verify

# Windows
mvnw.cmd verify
```

### Current Test Coverage

| Test Suite | Tests | Status |
|---|---|---|
| `FormControllerTest` | 5 | ✅ |
| `FieldControllerTest` | 5 | ✅ |
| `SubmissionControllerTest` | 3 | ✅ |
| `FormServiceImplTest` | 3 | ✅ |
| `FieldServiceImplTest` | 8 | ✅ |
| `SubmissionServiceImplTest` | 5 | ✅ |
| `FormValidationEngineTest` | 17 | ✅ |
| **Total** | **46** | **✅ 100% PASS** |

---

## API Documentation (Swagger UI)

After starting the application, the full interactive API documentation is available at:

**http://localhost:8081/swagger-ui/index.html**

To import into Postman:
1. Open the Swagger UI URL above
2. Copy the OpenAPI spec URL: `http://localhost:8081/v3/api-docs`
3. In Postman → Import → Link → paste the URL
