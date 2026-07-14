package com.example.formmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    private String description;

    @Column(name = "form_order")
    private Integer order;

    private String status;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("order ASC")
    private List<Field> fields = new ArrayList<>();

    public void addField(Field field) {
        fields.add(field);
        field.setForm(this);
    }
}
