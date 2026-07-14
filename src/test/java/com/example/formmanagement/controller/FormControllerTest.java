package com.example.formmanagement.controller;

import com.example.formmanagement.dto.form.FormRequest;
import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.service.FormService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FormControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private FormService formService;

    @InjectMocks
    private FormController formController;

    private FormResponse formResponse;
    private FormRequest formRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(formController).build();
        objectMapper = new ObjectMapper();

        formResponse = new FormResponse();
        formResponse.setId(1L);
        formResponse.setTitle("Khảo sát nhân viên");
        formResponse.setDescription("Form khảo sát hàng tháng");
        formResponse.setOrder(1);
        formResponse.setStatus("active");

        formRequest = new FormRequest();
        formRequest.setTitle("Khảo sát nhân viên");
        formRequest.setDescription("Form khảo sát hàng tháng");
        formRequest.setOrder(1);
        formRequest.setStatus("active");
    }

    @Test
    @DisplayName("POST /api/forms - Tạo form mới → trả về 201 CREATED")
    void createForm_ShouldReturn201AndFormResponse() throws Exception {
        when(formService.createForm(any(FormRequest.class))).thenReturn(formResponse);

        mockMvc.perform(post("/api/forms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(formRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Khảo sát nhân viên"))
                .andExpect(jsonPath("$.status").value("active"));

        verify(formService, times(1)).createForm(any(FormRequest.class));
    }

    @Test
    @DisplayName("GET /api/forms - Lấy tất cả forms → trả về 200 OK với danh sách")
    void getAllForms_ShouldReturn200AndList() throws Exception {
        when(formService.getAllForms()).thenReturn(List.of(formResponse));

        mockMvc.perform(get("/api/forms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Khảo sát nhân viên"));
    }

    @Test
    @DisplayName("GET /api/forms/{id} - Lấy form theo ID → trả về 200 OK")
    void getFormById_ShouldReturn200() throws Exception {
        when(formService.getFormById(1L)).thenReturn(formResponse);

        mockMvc.perform(get("/api/forms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Khảo sát nhân viên"));
    }

    @Test
    @DisplayName("PUT /api/forms/{id} - Cập nhật form → trả về 200 OK")
    void updateForm_ShouldReturn200() throws Exception {
        FormResponse updatedResponse = new FormResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Khảo sát cập nhật");
        updatedResponse.setStatus("active");

        when(formService.updateForm(eq(1L), any(FormRequest.class))).thenReturn(updatedResponse);

        formRequest.setTitle("Khảo sát cập nhật");
        mockMvc.perform(put("/api/forms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(formRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Khảo sát cập nhật"));
    }

    @Test
    @DisplayName("DELETE /api/forms/{id} - Xóa form → trả về 204 NO CONTENT")
    void deleteForm_ShouldReturn204() throws Exception {
        doNothing().when(formService).deleteForm(1L);

        mockMvc.perform(delete("/api/forms/1"))
                .andExpect(status().isNoContent());

        verify(formService, times(1)).deleteForm(1L);
    }
}
