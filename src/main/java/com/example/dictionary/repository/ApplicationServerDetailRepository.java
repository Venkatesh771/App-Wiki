package com.example.dictionary.repository;

import com.example.dictionary.entity.ApplicationServerDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationServerDetailRepository extends JpaRepository<ApplicationServerDetail, Long> {
    List<ApplicationServerDetail> findByEnvironment(String environment);
}
