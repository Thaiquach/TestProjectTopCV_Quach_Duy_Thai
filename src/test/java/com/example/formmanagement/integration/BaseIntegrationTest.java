package com.example.formmanagement.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Class cấu hình chung cho các test tích hợp
    // Ví dụ: cấu hình H2 database chung để các IT classes khác kế thừa
}
