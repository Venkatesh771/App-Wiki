package com.example.dictionary.repository;

import com.example.dictionary.entity.DatabaseServerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DatabaseServerDetailRepository extends JpaRepository<DatabaseServerDetail, Long> {
    List<DatabaseServerDetail> findByEnvironment(String environment);
}
