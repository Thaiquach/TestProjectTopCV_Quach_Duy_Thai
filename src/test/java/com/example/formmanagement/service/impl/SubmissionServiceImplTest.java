package com.example.formmanagement.service.impl;

import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.dto.submission.SubmissionRequest;
import com.example.formmanagement.dto.submission.SubmissionResponse;
import com.example.formmanagement.dto.submission.SubmissionValueRequest;
import com.example.formmanagement.entity.*;
import com.example.formmanagement.repository.FormRepository;
import com.example.formmanagement.repository.SubmissionRepository;
import com.example.formmanagement.service.FormService;
import com.example.formmanagement.validation.FieldValidatorEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * [Unit Test] Test thuần logic nghiệp vụ của SubmissionServiceImpl.
 * Mock toàn bộ dependencies (repository, validator, formService).
 */
@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private FormRepository formRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private FieldValidatorEngine validatorEngine;

    @Mock
    private FormService formService;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    private Form activeForm;
    private Form draftForm;
    private Field textField;
    private Submission savedSubmission;

    @BeforeEach
    void setUp() {
        textField = Field.builder()
                .id(10L)
                .label("Họ và tên")
                .type(FieldType.TEXT)
                .required(true)
                .build();

        activeForm = Form.builder()
                .id(1L)
                .title("Khảo sát nhân viên")
                .status("active")
                .fields(new ArrayList<>(List.of(textField)))
                .build();

        draftForm = Form.builder()
                .id(2L)
                .title("Form nháp")
                .status("draft")
                .fields(new ArrayList<>())
                .build();

        savedSubmission = Submission.builder()
                .id(100L)
                .form(activeForm)
                .values(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("getActiveForms - Trả về danh sách form có status active")
    void getActiveForms_ShouldReturnActiveFormList() {
        when(formRepository.findByStatusOrderByOrderAsc("active")).thenReturn(List.of(activeForm));
        FormResponse formResponse = new FormResponse();
        formResponse.setId(1L);
        formResponse.setTitle("Khảo sát nhân viên");
        when(formService.getFormById(1L)).thenReturn(formResponse);

        List<FormResponse> result = submissionService.getActiveForms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Khảo sát nhân viên", result.get(0).getTitle());
    }

    @Test
    @DisplayName("submitForm - Form không tồn tại → ném RuntimeException")
    void submitForm_WhenFormNotFound_ShouldThrowRuntimeException() {
        when(formRepository.findById(99L)).thenReturn(Optional.empty());

        SubmissionRequest request = new SubmissionRequest();
        request.setValues(List.of());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> submissionService.submitForm(99L, request)
        );

        assertTrue(ex.getMessage().contains("Không tìm thấy Form với id: 99"));
    }

    @Test
    @DisplayName("submitForm - Form ở trạng thái draft → ném IllegalArgumentException")
    void submitForm_WhenFormIsDraft_ShouldThrowIllegalArgumentException() {
        when(formRepository.findById(2L)).thenReturn(Optional.of(draftForm));

        SubmissionRequest request = new SubmissionRequest();
        request.setValues(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.submitForm(2L, request)
        );

        assertTrue(ex.getMessage().contains("Không thể submit form đang ở trạng thái draft."));
    }

    @Test
    @DisplayName("submitForm - Form active, dữ liệu hợp lệ → lưu và trả về SubmissionResponse")
    void submitForm_WithValidData_ShouldSaveAndReturnResponse() {
        when(formRepository.findById(1L)).thenReturn(Optional.of(activeForm));
        // validator không ném exception → hợp lệ
        doNothing().when(validatorEngine).validate(any(Field.class), any());
        when(submissionRepository.save(any(Submission.class))).thenReturn(savedSubmission);

        SubmissionValueRequest valueRequest = new SubmissionValueRequest();
        valueRequest.setFieldId(10L);
        valueRequest.setValue("Nguyen Van A");

        SubmissionRequest request = new SubmissionRequest();
        request.setValues(List.of(valueRequest));

        SubmissionResponse response = submissionService.submitForm(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getFormId());
        assertEquals("Khảo sát nhân viên", response.getFormTitle());
        verify(submissionRepository, times(1)).save(any(Submission.class));
        verify(validatorEngine, times(1)).validate(eq(textField), eq("Nguyen Van A"));
    }

    @Test
    @DisplayName("getAllSubmissions - Trả về danh sách tất cả submissions")
    void getAllSubmissions_ShouldReturnAllSubmissions() {
        when(submissionRepository.findAll()).thenReturn(List.of(savedSubmission));

        List<SubmissionResponse> result = submissionService.getAllSubmissions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFormId());
    }
}
