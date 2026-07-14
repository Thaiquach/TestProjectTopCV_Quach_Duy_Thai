package com.example.formmanagement.dto.submission;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmissionResponse {
    private Long id;
    private Long formId;
    private String formTitle;
    private LocalDateTime submittedAt;
    private List<SubmissionValueResponse> values;
}
