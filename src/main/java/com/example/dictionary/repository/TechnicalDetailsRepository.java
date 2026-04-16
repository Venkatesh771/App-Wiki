package com.example.dictionary.repository;

import com.example.dictionary.entity.TechnicalDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicalDetailsRepository extends JpaRepository<TechnicalDetails, Long> {
}
