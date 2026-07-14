package com.example.formmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @Enumerated(EnumType.STRING)
    private FieldType type;

    @Column(name = "field_order")
    private Integer order;

    private Boolean required;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "field_options", joinColumns = @JoinColumn(name = "field_id"))
    @Column(name = "option_value")
    private List<String> options;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    @ToString.Exclude // Tránh bị vòng lặp vô hạn khi toString
    private Form form;
}
