package com.example.dictionary.repository;

import com.example.dictionary.entity.BasicIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BasicIdentityRepository extends JpaRepository<BasicIdentity, Long> {
    Optional<BasicIdentity> findByBeatId(String beatId);
}
