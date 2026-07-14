package com.example.formmanagement.controller;

import com.example.formmanagement.dto.field.FieldRequest;
import com.example.formmanagement.dto.field.FieldResponse;
import com.example.formmanagement.service.FieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @PostMapping("/forms/{formId}/fields")
    public ResponseEntity<FieldResponse> createField(
            @PathVariable Long formId,
            @Valid @RequestBody FieldRequest request) {
        request.setFormId(formId);
        return new ResponseEntity<>(fieldService.createField(request), HttpStatus.CREATED);
    }

    @GetMapping("/fields")
    public ResponseEntity<List<FieldResponse>> getAllFields() {
        return ResponseEntity.ok(fieldService.getAllFields());
    }

    @GetMapping("/fields/{id}")
    public ResponseEntity<FieldResponse> getFieldById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldService.getFieldById(id));
    }

    @PutMapping("/forms/{formId}/fields/{id}")
    public ResponseEntity<FieldResponse> updateField(
            @PathVariable Long formId,
            @PathVariable Long id,
            @Valid @RequestBody FieldRequest request) {
        request.setFormId(formId);
        return ResponseEntity.ok(fieldService.updateField(id, request));
    }

    @DeleteMapping("/forms/{formId}/fields/{id}")
    public ResponseEntity<Void> deleteField(
            @PathVariable Long formId,
            @PathVariable Long id) {
        fieldService.deleteField(id);
        return ResponseEntity.noContent().build();
    }
}
