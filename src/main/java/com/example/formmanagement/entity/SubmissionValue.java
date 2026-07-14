package com.example.formmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    @ToString.Exclude
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    @ToString.Exclude
    private Field field;

    @Column(name = "field_value", columnDefinition = "TEXT")
    private String value;
}
