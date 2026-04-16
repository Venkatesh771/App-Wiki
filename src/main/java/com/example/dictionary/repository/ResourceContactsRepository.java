package com.example.dictionary.repository;

import com.example.dictionary.entity.ResourceContacts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceContactsRepository extends JpaRepository<ResourceContacts, Long> {
}
