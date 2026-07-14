package com.example.formmanagement.service.impl;

import com.example.formmanagement.dto.field.FieldResponse;
import com.example.formmanagement.dto.form.FormRequest;
import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.entity.Field;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.repository.FormRepository;
import com.example.formmanagement.service.FormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;

    @Override
    public FormResponse createForm(FormRequest request) {
        Form form = Form.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .order(request.getOrder())
                .status(request.getStatus())
                .build();
        return mapToResponse(formRepository.save(form));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormResponse> getAllForms() {
        return formRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FormResponse getFormById(Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Form với id: " + id));
        return mapToResponse(form);
    }

    @Override
    public FormResponse updateForm(Long id, FormRequest request) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Form với id: " + id));
        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        form.setOrder(request.getOrder());
        form.setStatus(request.getStatus());
        return mapToResponse(formRepository.save(form));
    }

    @Override
    public void deleteForm(Long id) {
        if (!formRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Form với id: " + id);
        }
        formRepository.deleteById(id);
    }

    private FormResponse mapToResponse(Form form) {
        FormResponse response = new FormResponse();
        response.setId(form.getId());
        response.setTitle(form.getTitle());
        response.setDescription(form.getDescription());
        response.setOrder(form.getOrder());
        response.setStatus(form.getStatus());

        if (form.getFields() != null) {
            List<FieldResponse> fieldResponses = form.getFields().stream()
                    .map(this::mapFieldToResponse)
                    .collect(Collectors.toList());
            response.setFields(fieldResponses);
        }
        return response;
    }

    private FieldResponse mapFieldToResponse(Field field) {
        FieldResponse response = new FieldResponse();
        response.setId(field.getId());
        response.setLabel(field.getLabel());
        response.setType(field.getType());
        response.setOrder(field.getOrder());
        response.setRequired(field.getRequired());
        response.setOptions(field.getOptions());
        return response;
    }
}
