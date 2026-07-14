package com.example.formmanagement.service;

import com.example.formmanagement.dto.form.FormRequest;
import com.example.formmanagement.dto.form.FormResponse;

import java.util.List;

public interface FormService {

    FormResponse createForm(FormRequest request);

    List<FormResponse> getAllForms();

    FormResponse getFormById(Long id);

    FormResponse updateForm(Long id, FormRequest request);

    void deleteForm(Long id);
}
