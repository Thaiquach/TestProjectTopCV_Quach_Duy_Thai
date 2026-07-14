package com.example.formmanagement.service;

import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.dto.submission.SubmissionRequest;
import com.example.formmanagement.dto.submission.SubmissionResponse;

import java.util.List;

public interface SubmissionService {

    List<FormResponse> getActiveForms();

    SubmissionResponse submitForm(Long formId, SubmissionRequest request);

    List<SubmissionResponse> getAllSubmissions();
}
