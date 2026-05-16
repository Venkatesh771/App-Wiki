package com.example.dictionary.repository;

import com.example.dictionary.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByL15NameAndL2Name(String l15Name, String l2Name);

    @Query("SELECT DISTINCT g.l2Name FROM Group g WHERE g.l2Name IS NOT NULL AND g.l2Name <> '' ORDER BY g.l2Name")
    List<String> findDistinctL2Names();

    long countByL2Name(String l2Name);
}
