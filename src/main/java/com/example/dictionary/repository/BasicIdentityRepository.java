package com.example.dictionary.repository;

import com.example.dictionary.entity.BasicIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicIdentityRepository extends JpaRepository<BasicIdentity, Long> {
}
