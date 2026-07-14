package com.example.formmanagement.controller;

import com.example.formmanagement.dto.form.FormRequest;
import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.service.FormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @PostMapping
    public ResponseEntity<FormResponse> createForm(@Valid @RequestBody FormRequest request) {
        return new ResponseEntity<>(formService.createForm(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FormResponse>> getAllForms() {
        return ResponseEntity.ok(formService.getAllForms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormResponse> getFormById(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getFormById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormResponse> updateForm(@PathVariable Long id, @Valid @RequestBody FormRequest request) {
        return ResponseEntity.ok(formService.updateForm(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        formService.deleteForm(id);
        return ResponseEntity.noContent().build();
    }
}
