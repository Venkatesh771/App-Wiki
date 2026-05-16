package com.example.dictionary.repository;

import com.example.dictionary.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByTypeOrderByCreatedAtDesc(String type);

    List<ActivityLog> findByTypeInOrderByCreatedAtDesc(Collection<String> types);
}
