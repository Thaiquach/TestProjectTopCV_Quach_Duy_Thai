package com.example.formmanagement.controller;

import com.example.formmanagement.dto.form.FormResponse;
import com.example.formmanagement.dto.submission.SubmissionRequest;
import com.example.formmanagement.dto.submission.SubmissionResponse;
import com.example.formmanagement.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping("/forms/active")
    public ResponseEntity<List<FormResponse>> getActiveForms() {
        return ResponseEntity.ok(submissionService.getActiveForms());
    }

    @PostMapping("/forms/{id}/submit")
    public ResponseEntity<SubmissionResponse> submitForm(
            @PathVariable Long id, 
            @Valid @RequestBody SubmissionRequest request) {
        return new ResponseEntity<>(submissionService.submitForm(id, request), HttpStatus.CREATED);
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<SubmissionResponse>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }
}
