package com.example.formmanagement.repository;

import com.example.formmanagement.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {
    
    List<Field> findByFormIdOrderByOrderAsc(Long formId);
}
