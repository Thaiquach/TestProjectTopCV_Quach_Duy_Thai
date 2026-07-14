package com.example.formmanagement.repository;

import com.example.formmanagement.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    
    List<Form> findByStatus(String status);

    List<Form> findByStatusOrderByOrderAsc(String status);
}
