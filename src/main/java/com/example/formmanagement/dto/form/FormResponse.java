package com.example.formmanagement.dto.form;

import com.example.formmanagement.dto.field.FieldResponse;
import lombok.Data;
import java.util.List;

@Data
public class FormResponse {
    private Long id;
    private String title;
    private String description;
    private Integer order;
    private String status;
    private List<FieldResponse> fields;
}
