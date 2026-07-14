package com.example.formmanagement.dto.submission;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionValueRequest {
    @NotNull(message = "fieldId không được để trống")
    private Long fieldId;
    
    private String value;
}
