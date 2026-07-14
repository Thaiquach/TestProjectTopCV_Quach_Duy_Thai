package com.example.formmanagement.service.impl;

import com.example.formmanagement.dto.field.FieldRequest;
import com.example.formmanagement.dto.field.FieldResponse;
import com.example.formmanagement.entity.Field;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.repository.FieldRepository;
import com.example.formmanagement.repository.FormRepository;
import com.example.formmanagement.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final FormRepository formRepository;

    @Override
    public FieldResponse createField(FieldRequest request) {
        if (request.getFormId() == null) {
            throw new IllegalArgumentException("formId không được để trống");
        }
        Form form = formRepository.findById(request.getFormId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Form với id: " + request.getFormId()));

        Field field = Field.builder()
                .label(request.getLabel())
                .type(request.getType())
                .order(request.getOrder())
                .required(request.getRequired())
                .options(request.getOptions())
                .form(form)
                .build();
        return mapToResponse(fieldRepository.save(field));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldResponse> getAllFields() {
        return fieldRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FieldResponse getFieldById(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Field với id: " + id));
        return mapToResponse(field);
    }

    @Override
    public FieldResponse updateField(Long id, FieldRequest request) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Field với id: " + id));

        if (request.getFormId() != null && !request.getFormId().equals(field.getForm().getId())) {
            Form newForm = formRepository.findById(request.getFormId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Form với id: " + request.getFormId()));
            field.setForm(newForm);
        }

        field.setLabel(request.getLabel());
        field.setType(request.getType());
        field.setOrder(request.getOrder());
        field.setRequired(request.getRequired());
        field.setOptions(request.getOptions());
        return mapToResponse(fieldRepository.save(field));
    }

    @Override
    public void deleteField(Long id) {
        if (!fieldRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy Field với id: " + id);
        }
        fieldRepository.deleteById(id);
    }

    private FieldResponse mapToResponse(Field field) {
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
