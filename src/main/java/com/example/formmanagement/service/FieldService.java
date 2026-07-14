package com.example.formmanagement.service;

import com.example.formmanagement.dto.field.FieldRequest;
import com.example.formmanagement.dto.field.FieldResponse;

import java.util.List;

public interface FieldService {

    FieldResponse createField(FieldRequest request);

    List<FieldResponse> getAllFields();

    FieldResponse getFieldById(Long id);

    FieldResponse updateField(Long id, FieldRequest request);

    void deleteField(Long id);
}
