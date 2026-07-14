package com.example.formmanagement.controller;

import com.example.formmanagement.dto.field.FieldRequest;
import com.example.formmanagement.dto.field.FieldResponse;
import com.example.formmanagement.entity.FieldType;
import com.example.formmanagement.service.FieldService;
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
class FieldControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private FieldService fieldService;

    @InjectMocks
    private FieldController fieldController;

    private FieldResponse fieldResponse;
    private FieldRequest fieldRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fieldController).build();
        objectMapper = new ObjectMapper();

        fieldResponse = new FieldResponse();
        fieldResponse.setId(10L);
        fieldResponse.setLabel("Họ và tên");
        fieldResponse.setType(FieldType.TEXT);
        fieldResponse.setOrder(1);
        fieldResponse.setRequired(true);

        fieldRequest = new FieldRequest();
        fieldRequest.setLabel("Họ và tên");
        fieldRequest.setType(FieldType.TEXT);
        fieldRequest.setOrder(1);
        fieldRequest.setRequired(true);
    }

    @Test
    @DisplayName("POST /api/forms/{formId}/fields - Tạo field → trả về 201 CREATED")
    void createField_ShouldReturn201AndFieldResponse() throws Exception {
        when(fieldService.createField(any(FieldRequest.class))).thenReturn(fieldResponse);

        mockMvc.perform(post("/api/forms/1/fields")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fieldRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.label").value("Họ và tên"))
                .andExpect(jsonPath("$.type").value("TEXT"))
                .andExpect(jsonPath("$.required").value(true));

        verify(fieldService, times(1)).createField(any(FieldRequest.class));
    }

    @Test
    @DisplayName("GET /api/fields - Lấy tất cả fields → trả về 200 OK")
    void getAllFields_ShouldReturn200AndList() throws Exception {
        when(fieldService.getAllFields()).thenReturn(List.of(fieldResponse));

        mockMvc.perform(get("/api/fields"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].label").value("Họ và tên"));
    }

    @Test
    @DisplayName("GET /api/fields/{id} - Lấy field theo ID → trả về 200 OK")
    void getFieldById_ShouldReturn200() throws Exception {
        when(fieldService.getFieldById(10L)).thenReturn(fieldResponse);

        mockMvc.perform(get("/api/fields/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.label").value("Họ và tên"));
    }

    @Test
    @DisplayName("PUT /api/forms/{formId}/fields/{id} - Cập nhật field → trả về 200 OK")
    void updateField_ShouldReturn200() throws Exception {
        FieldResponse updated = new FieldResponse();
        updated.setId(10L);
        updated.setLabel("Tên đầy đủ");
        updated.setType(FieldType.TEXT);

        when(fieldService.updateField(eq(10L), any(FieldRequest.class))).thenReturn(updated);

        fieldRequest.setLabel("Tên đầy đủ");
        mockMvc.perform(put("/api/forms/1/fields/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fieldRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Tên đầy đủ"));
    }

    @Test
    @DisplayName("DELETE /api/forms/{formId}/fields/{id} - Xóa field → trả về 204 NO CONTENT")
    void deleteField_ShouldReturn204() throws Exception {
        doNothing().when(fieldService).deleteField(10L);

        mockMvc.perform(delete("/api/forms/1/fields/10"))
                .andExpect(status().isNoContent());

        verify(fieldService, times(1)).deleteField(10L);
    }
}
