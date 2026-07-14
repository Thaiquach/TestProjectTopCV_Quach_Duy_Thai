package com.example.formmanagement.dto.submission;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class SubmissionRequest {
    @NotEmpty(message = "Danh sách câu trả lời không được để trống")
    @Valid
    private List<SubmissionValueRequest> values;
}
