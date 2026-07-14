package com.example.formmanagement.dto.form;

import lombok.Data;

@Data
public class FormRequest {
    private String title;
    private String description;
    private Integer order;
    private String status;
}
