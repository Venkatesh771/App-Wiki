package com.example.dictionary.repository;

import com.example.dictionary.entity.CloudDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CloudDetailRepository extends JpaRepository<CloudDetail, Long> {
    List<CloudDetail> findByEnvironment(String environment);
}
