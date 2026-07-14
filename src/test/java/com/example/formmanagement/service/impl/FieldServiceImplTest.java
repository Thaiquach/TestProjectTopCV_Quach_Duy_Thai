package com.example.formmanagement.service.impl;

import com.example.formmanagement.dto.field.FieldRequest;
import com.example.formmanagement.dto.field.FieldResponse;
import com.example.formmanagement.entity.Field;
import com.example.formmanagement.entity.FieldType;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.repository.FieldRepository;
import com.example.formmanagement.repository.FormRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldServiceImplTest {

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private FormRepository formRepository;

    @InjectMocks
    private FieldServiceImpl fieldService;

    private Form form;
    private Field field;
    private FieldRequest fieldRequest;

    @BeforeEach
    void setUp() {
        form = Form.builder()
                .id(1L)
                .title("Khảo sát nhân viên")
                .status("active")
                .build();

        field = Field.builder()
                .id(10L)
                .label("Họ và tên")
                .type(FieldType.TEXT)
                .order(1)
                .required(true)
                .form(form)
                .build();

        fieldRequest = new FieldRequest();
        fieldRequest.setFormId(1L);
        fieldRequest.setLabel("Họ và tên");
        fieldRequest.setType(FieldType.TEXT);
        fieldRequest.setOrder(1);
        fieldRequest.setRequired(true);
    }

    @Test
    @DisplayName("createField - Tạo field thành công khi form tồn tại")
    void createField_WhenFormExists_ShouldReturnFieldResponse() {
        when(formRepository.findById(1L)).thenReturn(Optional.of(form));
        when(fieldRepository.save(any(Field.class))).thenReturn(field);

        FieldResponse response = fieldService.createField(fieldRequest);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Họ và tên", response.getLabel());
        assertEquals(FieldType.TEXT, response.getType());
        assertTrue(response.getRequired());
        verify(fieldRepository, times(1)).save(any(Field.class));
    }

    @Test
    @DisplayName("createField - formId null → ném IllegalArgumentException")
    void createField_WhenFormIdNull_ShouldThrowIllegalArgumentException() {
        fieldRequest.setFormId(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> fieldService.createField(fieldRequest)
        );

        assertEquals("formId không được để trống", ex.getMessage());
        verifyNoInteractions(fieldRepository);
    }

    @Test
    @DisplayName("createField - Form không tồn tại → ném RuntimeException")
    void createField_WhenFormNotFound_ShouldThrowRuntimeException() {
        when(formRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> fieldService.createField(fieldRequest)
        );

        assertTrue(ex.getMessage().contains("Không tìm thấy Form với id: 1"));
        verifyNoInteractions(fieldRepository);
    }

    @Test
    @DisplayName("getAllFields - Trả về danh sách tất cả fields")
    void getAllFields_ShouldReturnAllFields() {
        when(fieldRepository.findAll()).thenReturn(List.of(field));

        List<FieldResponse> result = fieldService.getAllFields();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Họ và tên", result.get(0).getLabel());
    }

    @Test
    @DisplayName("getFieldById - Tìm thấy field → trả về FieldResponse")
    void getFieldById_WhenExists_ShouldReturnFieldResponse() {
        when(fieldRepository.findById(10L)).thenReturn(Optional.of(field));

        FieldResponse response = fieldService.getFieldById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Họ và tên", response.getLabel());
    }

    @Test
    @DisplayName("getFieldById - Không tìm thấy → ném RuntimeException")
    void getFieldById_WhenNotFound_ShouldThrowRuntimeException() {
        when(fieldRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> fieldService.getFieldById(99L)
        );

        assertTrue(ex.getMessage().contains("Không tìm thấy Field với id: 99"));
    }

    @Test
    @DisplayName("deleteField - Xóa field thành công khi tồn tại")
    void deleteField_WhenExists_ShouldCallDeleteById() {
        when(fieldRepository.existsById(10L)).thenReturn(true);
        doNothing().when(fieldRepository).deleteById(10L);

        assertDoesNotThrow(() -> fieldService.deleteField(10L));
        verify(fieldRepository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("deleteField - Không tồn tại → ném RuntimeException")
    void deleteField_WhenNotFound_ShouldThrowRuntimeException() {
        when(fieldRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> fieldService.deleteField(99L)
        );

        assertTrue(ex.getMessage().contains("Không tìm thấy Field với id: 99"));
        verify(fieldRepository, never()).deleteById(any());
    }
}
