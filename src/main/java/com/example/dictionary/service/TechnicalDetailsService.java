package com.example.dictionary.service;

import com.example.dictionary.entity.TechnicalDetails;
import com.example.dictionary.repository.TechnicalDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnicalDetailsService {
    @Autowired
    private TechnicalDetailsRepository repository;

    public List<TechnicalDetails> findAll() {
        return repository.findAll();
    }

    public Optional<TechnicalDetails> findById(Long id) {
        return repository.findById(id);
    }    public TechnicalDetails save(TechnicalDetails entity) {
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
    private void cleanupEntity(TechnicalDetails entity) {
        if (entity.getAppUrlsDevTest() != null && (entity.getAppUrlsDevTest().trim().isEmpty() || entity.getAppUrlsDevTest().trim().equals("Select"))) {
            entity.setAppUrlsDevTest(null);
        }
        if (entity.getGithubRepos() != null && (entity.getGithubRepos().trim().isEmpty() || entity.getGithubRepos().trim().equals("Select"))) {
            entity.setGithubRepos(null);
        }
        if (entity.getLbVipDetails() != null && (entity.getLbVipDetails().trim().isEmpty() || entity.getLbVipDetails().trim().equals("Select"))) {
            entity.setLbVipDetails(null);
        }
        if (entity.getExternalInternal() != null && (entity.getExternalInternal().trim().isEmpty() || entity.getExternalInternal().trim().equals("Select"))) {
            entity.setExternalInternal(null);
        }
        if (entity.getUpstreamSystem() != null && (entity.getUpstreamSystem().trim().isEmpty() || entity.getUpstreamSystem().trim().equals("Select"))) {
            entity.setUpstreamSystem(null);
        }
        if (entity.getAkanaMulesoft() != null && (entity.getAkanaMulesoft().trim().isEmpty() || entity.getAkanaMulesoft().trim().equals("Select"))) {
            entity.setAkanaMulesoft(null);
        }
        if (entity.getKafkaTopic() != null && (entity.getKafkaTopic().trim().isEmpty() || entity.getKafkaTopic().trim().equals("Select"))) {
            entity.setKafkaTopic(null);
        }
        if (entity.getAzureClientIds() != null && (entity.getAzureClientIds().trim().isEmpty() || entity.getAzureClientIds().trim().equals("Select"))) {
            entity.setAzureClientIds(null);
        }
    }
}
