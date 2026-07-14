package com.example.formmanagement.service.impl;

import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.dto.submission.SubmissionRequest;
import com.example.formmanagement.dto.submission.SubmissionResponse;
import com.example.formmanagement.dto.submission.SubmissionValueResponse;
import com.example.formmanagement.entity.Field;
import com.example.formmanagement.entity.Form;
import com.example.formmanagement.entity.Submission;
import com.example.formmanagement.entity.SubmissionValue;
import com.example.formmanagement.repository.FormRepository;
import com.example.formmanagement.repository.SubmissionRepository;
import com.example.formmanagement.service.FormService;
import com.example.formmanagement.service.SubmissionService;
import com.example.formmanagement.validation.FieldValidatorEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    private final FormRepository formRepository;
    private final SubmissionRepository submissionRepository;
    private final FieldValidatorEngine validatorEngine;
    private final FormService formService;

    @Override
    @Transactional(readOnly = true)
    public List<FormResponse> getActiveForms() {
        return formRepository.findByStatusOrderByOrderAsc("active").stream()
                .map(form -> formService.getFormById(form.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public SubmissionResponse submitForm(Long formId, SubmissionRequest request) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Form với id: " + formId));

        if (!"active".equalsIgnoreCase(form.getStatus())) {
            throw new IllegalArgumentException("Không thể submit form đang ở trạng thái draft.");
        }

        Submission submission = Submission.builder()
                .form(form)
                .build();

        Map<Long, String> requestValues = request.getValues().stream()
                .collect(Collectors.toMap(
                        v -> v.getFieldId(),
                        v -> v.getValue() != null ? v.getValue() : "",
                        (v1, v2) -> v1
                ));

        for (Field field : form.getFields()) {
            String submittedValue = requestValues.get(field.getId());
            validatorEngine.validate(field, submittedValue);

            if (submittedValue != null && !submittedValue.trim().isEmpty()) {
                SubmissionValue subValue = SubmissionValue.builder()
                        .field(field)
                        .value(submittedValue)
                        .build();
                submission.addValue(subValue);
            }
        }

        Submission savedSubmission = submissionRepository.save(submission);
        return mapToResponse(savedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubmissionResponse mapToResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setFormId(submission.getForm().getId());
        response.setFormTitle(submission.getForm().getTitle());
        response.setSubmittedAt(submission.getSubmittedAt());

        List<SubmissionValueResponse> valueResponses = submission.getValues().stream()
                .map(val -> {
                    SubmissionValueResponse vr = new SubmissionValueResponse();
                    vr.setId(val.getId());
                    vr.setFieldId(val.getField().getId());
                    vr.setFieldLabel(val.getField().getLabel());
                    vr.setValue(val.getValue());
                    return vr;
                }).collect(Collectors.toList());

        response.setValues(valueResponses);
        return response;
    }
}
