package com.example.formmanagement.service.impl;

import com.example.formmanagement.dto.form.FormRequest;
import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.repository.FormRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FormServiceImplTest {

    @Mock
    private FormRepository formRepository;

    @InjectMocks
    private FormServiceImpl formService;

    private Form form;
    private FormRequest formRequest;

    @BeforeEach
    void setUp() {
        form = Form.builder()
                .id(1L)
                .title("Test Form")
                .description("Description")
                .order(1)
                .status("active")
                .build();

        formRequest = new FormRequest();
        formRequest.setTitle("Test Form");
        formRequest.setDescription("Description");
        formRequest.setOrder(1);
        formRequest.setStatus("active");
    }

    @Test
    void testCreateForm() {
        when(formRepository.save(any(Form.class))).thenReturn(form);

        FormResponse response = formService.createForm(formRequest);

        assertNotNull(response);
        assertEquals("Test Form", response.getTitle());
        verify(formRepository, times(1)).save(any(Form.class));
    }

    @Test
    void testGetFormById_Success() {
        when(formRepository.findById(1L)).thenReturn(Optional.of(form));

        FormResponse response = formService.getFormById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Form", response.getTitle());
    }

    @Test
    void testGetFormById_NotFound() {
        when(formRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            formService.getFormById(1L);
        });

        assertEquals("Không tìm thấy Form với id: 1", exception.getMessage());
    }
}
