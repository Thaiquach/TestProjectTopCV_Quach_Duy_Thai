package com.example.formmanagement.dto.submission;

import lombok.Data;

@Data
public class SubmissionValueResponse {
    private Long id;
    private Long fieldId;
    private String fieldLabel;
    private String value;
}
