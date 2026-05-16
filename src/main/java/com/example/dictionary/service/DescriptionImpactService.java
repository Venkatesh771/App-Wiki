package com.example.dictionary.service;

import com.example.dictionary.entity.DescriptionImpact;
import com.example.dictionary.repository.DescriptionImpactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DescriptionImpactService {
    @Autowired
    private DescriptionImpactRepository repository;

    public List<DescriptionImpact> findAll() {
        return repository.findAll();
    }

    public Optional<DescriptionImpact> findById(Long id) {
        return repository.findById(id);
    }

    public DescriptionImpact save(DescriptionImpact entity) {

        cleanupEntity(entity);
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void cleanupEntity(DescriptionImpact entity) {
        if (entity.getApplicationDescription() != null && (entity.getApplicationDescription().trim().isEmpty() || entity.getApplicationDescription().trim().equals("Select"))) {
            entity.setApplicationDescription(null);
        }
        if (entity.getBusinessImpactDescription() != null && (entity.getBusinessImpactDescription().trim().isEmpty() || entity.getBusinessImpactDescription().trim().equals("Select"))) {
            entity.setBusinessImpactDescription(null);
        }
        if (entity.getFinancialImpact() != null && (entity.getFinancialImpact().trim().isEmpty() || entity.getFinancialImpact().trim().equals("Select"))) {
            entity.setFinancialImpact(null);
        }
        if (entity.getUserAccessReview() != null && (entity.getUserAccessReview().trim().isEmpty() || entity.getUserAccessReview().trim().equals("Select"))) {
            entity.setUserAccessReview(null);
        }
    }
}
