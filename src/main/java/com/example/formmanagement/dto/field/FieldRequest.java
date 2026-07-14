package com.example.formmanagement.dto.field;

import com.example.formmanagement.entity.FieldType;
import lombok.Data;
import java.util.List;

@Data
public class FieldRequest {
    private Long formId;
    private String label;
    private FieldType type;
    private Integer order;
    private Boolean required;
    private List<String> options;
}
