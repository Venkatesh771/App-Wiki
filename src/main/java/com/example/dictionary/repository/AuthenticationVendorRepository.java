package com.example.dictionary.repository;

import com.example.dictionary.entity.AuthenticationVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationVendorRepository extends JpaRepository<AuthenticationVendor, Long> {
}
