package com.example.dictionary.repository;

import com.example.dictionary.entity.DescriptionImpact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptionImpactRepository extends JpaRepository<DescriptionImpact, Long> {
}
