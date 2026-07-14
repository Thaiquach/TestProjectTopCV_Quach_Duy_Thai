-- Full database schema for the Form Management project
-- Run this script on a MySQL/MariaDB server to create the database and tables.

CREATE DATABASE IF NOT EXISTS form_management_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE form_management_db;

CREATE TABLE IF NOT EXISTS forms (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255)    NOT NULL,
    description TEXT            NULL,
    form_order  INT             NULL,
    status      VARCHAR(20)     NOT NULL DEFAULT 'draft' COMMENT 'Chỉ nhận giá trị: active hoặc draft',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fields (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    label       VARCHAR(255)    NOT NULL COMMENT 'Tên hiển thị của field',
    type        VARCHAR(20)     NOT NULL COMMENT 'Loại field: TEXT, NUMBER, DATE, COLOR, SELECT',
    field_order INT             NULL     COMMENT 'Thứ tự hiển thị trong form',
    required    BOOLEAN         NOT NULL DEFAULT FALSE,
    form_id     BIGINT          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_fields_form FOREIGN KEY (form_id) REFERENCES forms(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS field_options (
    field_id        BIGINT          NOT NULL,
    option_value    VARCHAR(255)    NOT NULL,
    CONSTRAINT fk_options_field FOREIGN KEY (field_id) REFERENCES fields(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS submissions (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    form_id         BIGINT      NOT NULL,
    submitted_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_submissions_form FOREIGN KEY (form_id) REFERENCES forms(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS submission_values (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    submission_id   BIGINT  NOT NULL,
    field_id        BIGINT  NOT NULL,
    field_value     TEXT    NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sv_submission FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE,
    CONSTRAINT fk_sv_field      FOREIGN KEY (field_id)      REFERENCES fields(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
