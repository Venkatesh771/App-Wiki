package com.example.dictionary.repository;

import com.example.dictionary.entity.BasicIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BasicIdentityRepository extends JpaRepository<BasicIdentity, Long> {
    Optional<BasicIdentity> findByBeatId(String beatId);
    Optional<BasicIdentity> findByBeatIdIgnoreCase(String beatId);
    Optional<BasicIdentity> findByApplicationNameIgnoreCase(String applicationName);
    Optional<BasicIdentity> findByBeatIdIgnoreCaseAndIdNot(String beatId, Long id);
    Optional<BasicIdentity> findByApplicationNameIgnoreCaseAndIdNot(String applicationName, Long id);

    @Query("SELECT b FROM BasicIdentity b WHERE LOWER(b.beatId) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(b.applicationName) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<BasicIdentity> searchByBeatIdOrApplicationName(@Param("q") String q);

    @Query("SELECT b FROM BasicIdentity b WHERE b.active IS NULL OR b.active = true")
    List<BasicIdentity> findAllActive();

    @Query("SELECT DISTINCT b.assignmentGroup FROM BasicIdentity b WHERE b.assignmentGroup IS NOT NULL AND b.assignmentGroup <> '' ORDER BY b.assignmentGroup")
    List<String> findDistinctAssignmentGroups();

    @Query("SELECT COUNT(b) FROM BasicIdentity b WHERE b.assignmentGroup = :group AND (b.active IS NULL OR b.active = true)")
    long countByAssignmentGroup(@Param("group") String group);
}
