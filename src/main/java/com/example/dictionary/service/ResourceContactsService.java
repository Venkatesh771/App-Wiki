package com.example.dictionary.service;

import com.example.dictionary.entity.ResourceContacts;
import com.example.dictionary.repository.ResourceContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceContactsService {
    @Autowired
    private ResourceContactsRepository repository;

    public List<ResourceContacts> findAll() {
        return repository.findAll();
    }

    public Optional<ResourceContacts> findById(Long id) {
        return repository.findById(id);
    }

    public ResourceContacts save(ResourceContacts entity) {
        // Clean up empty and placeholder values
        cleanupEntity(entity);
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    /**
     * Convert empty strings and placeholder values to null
     * This ensures dropdowns that weren't selected don't store "Select" text
     */
    private void cleanupEntity(ResourceContacts entity) {
        if (entity.getOffshorePrimary() != null && (entity.getOffshorePrimary().trim().isEmpty() || entity.getOffshorePrimary().trim().equals("Select"))) {
            entity.setOffshorePrimary(null);
        }
        if (entity.getOffshoreSecondary() != null && (entity.getOffshoreSecondary().trim().isEmpty() || entity.getOffshoreSecondary().trim().equals("Select"))) {
            entity.setOffshoreSecondary(null);
        }
        if (entity.getOffshoreTertiary() != null && (entity.getOffshoreTertiary().trim().isEmpty() || entity.getOffshoreTertiary().trim().equals("Select"))) {
            entity.setOffshoreTertiary(null);
        }
        if (entity.getOnshorePrimary() != null && (entity.getOnshorePrimary().trim().isEmpty() || entity.getOnshorePrimary().trim().equals("Select"))) {
            entity.setOnshorePrimary(null);
        }
        if (entity.getOnshoreSecondary() != null && (entity.getOnshoreSecondary().trim().isEmpty() || entity.getOnshoreSecondary().trim().equals("Select"))) {
            entity.setOnshoreSecondary(null);
        }
        if (entity.getOnshoreTertiary() != null && (entity.getOnshoreTertiary().trim().isEmpty() || entity.getOnshoreTertiary().trim().equals("Select"))) {
            entity.setOnshoreTertiary(null);
        }
    }
}
