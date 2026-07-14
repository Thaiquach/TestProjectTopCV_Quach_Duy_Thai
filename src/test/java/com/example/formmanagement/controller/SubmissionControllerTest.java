package com.example.formmanagement.controller;

import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.dto.submission.SubmissionRequest;
import com.example.formmanagement.dto.submission.SubmissionResponse;
import com.example.formmanagement.dto.submission.SubmissionValueRequest;
import com.example.formmanagement.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private SubmissionController submissionController;

    private SubmissionResponse submissionResponse;
    private FormResponse activeForm;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(submissionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        activeForm = new FormResponse();
        activeForm.setId(1L);
        activeForm.setTitle("Khảo sát tháng 7");
        activeForm.setStatus("active");

        submissionResponse = new SubmissionResponse();
        submissionResponse.setId(100L);
        submissionResponse.setFormId(1L);
        submissionResponse.setFormTitle("Khảo sát tháng 7");
        submissionResponse.setSubmittedAt(LocalDateTime.now());
        submissionResponse.setValues(List.of());
    }

    @Test
    @DisplayName("GET /api/forms/active - Lấy các form đang hoạt động → 200 OK")
    void getActiveForms_ShouldReturn200AndList() throws Exception {
        when(submissionService.getActiveForms()).thenReturn(List.of(activeForm));

        mockMvc.perform(get("/api/forms/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("active"))
                .andExpect(jsonPath("$[0].title").value("Khảo sát tháng 7"));
    }

    @Test
    @DisplayName("POST /api/forms/{id}/submit - Nộp form hợp lệ → 201 CREATED")
    void submitForm_WithValidRequest_ShouldReturn201() throws Exception {
        SubmissionValueRequest valueRequest = new SubmissionValueRequest();
        valueRequest.setFieldId(10L);
        valueRequest.setValue("Nguyen Van A");

        SubmissionRequest request = new SubmissionRequest();
        request.setValues(List.of(valueRequest));

        when(submissionService.submitForm(eq(1L), any(SubmissionRequest.class)))
                .thenReturn(submissionResponse);

        mockMvc.perform(post("/api/forms/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.formId").value(1L))
                .andExpect(jsonPath("$.formTitle").value("Khảo sát tháng 7"));

        verify(submissionService, times(1)).submitForm(eq(1L), any(SubmissionRequest.class));
    }

    @Test
    @DisplayName("GET /api/submissions - Lấy tất cả submissions → 200 OK")
    void getAllSubmissions_ShouldReturn200AndList() throws Exception {
        when(submissionService.getAllSubmissions()).thenReturn(List.of(submissionResponse));

        mockMvc.perform(get("/api/submissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].formTitle").value("Khảo sát tháng 7"));
    }
}
