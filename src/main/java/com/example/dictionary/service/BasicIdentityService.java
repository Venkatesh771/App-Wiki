package com.example.dictionary.service;

import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BasicIdentityService {
    private final BasicIdentityRepository repository;

    public BasicIdentityService(BasicIdentityRepository repository) {
        this.repository = repository;
    }

    public List<BasicIdentity> getAll() {
        return repository.findAllActive();
    }

    public List<BasicIdentity> getAllIncludingInactive() {
        return repository.findAll();
    }

    @Transactional
    public boolean deactivate(Long id) {
        return repository.findById(id).map(app -> {
            app.setActive(false);
            repository.save(app);
            return true;
        }).orElse(false);
    }

    public Optional<BasicIdentity> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<BasicIdentity> getByIdWithDetails(Long id) {
        Optional<BasicIdentity> appOpt = repository.findById(id);
        appOpt.ifPresent(app -> {
            Hibernate.initialize(app.getDescriptionImpacts());
            Hibernate.initialize(app.getAuthenticationVendors());
            Hibernate.initialize(app.getTechnicalDetails());
            Hibernate.initialize(app.getResourceContacts());
            Hibernate.initialize(app.getApplicationServerDetails());
            Hibernate.initialize(app.getCloudDetails());
            Hibernate.initialize(app.getDatabaseServerDetails());
        });
        return appOpt;
    }

    public boolean existsByBeatId(String beatId) {
        return repository.findByBeatIdIgnoreCase(beatId.trim()).isPresent();
    }

    public boolean existsByApplicationName(String applicationName) {
        return repository.findByApplicationNameIgnoreCase(applicationName.trim()).isPresent();
    }

    public boolean existsByBeatIdExcludingId(String beatId, Long excludeId) {
        return repository.findByBeatIdIgnoreCaseAndIdNot(beatId.trim(), excludeId).isPresent();
    }

    public boolean existsByApplicationNameExcludingId(String applicationName, Long excludeId) {
        return repository.findByApplicationNameIgnoreCaseAndIdNot(applicationName.trim(), excludeId).isPresent();
    }

    public BasicIdentity save(BasicIdentity entity) {
        // Clean up empty and placeholder values
        cleanupEntity(entity);
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<BasicIdentity> search(String q) {
        return repository.searchByBeatIdOrApplicationName(q);
    }

    public List<String> getDistinctAssignmentGroups() {
        return repository.findDistinctAssignmentGroups();
    }

    /**
     * Convert empty strings and placeholder values to null
     * This ensures dropdowns that weren't selected don't store "Select" text
     */
    private void cleanupEntity(BasicIdentity entity) {
        if (entity.getBeatId() != null && (entity.getBeatId().trim().isEmpty() || entity.getBeatId().trim().equals("Select"))) {
            entity.setBeatId(null);
        }
        if (entity.getApplicationName() != null && (entity.getApplicationName().trim().isEmpty() || entity.getApplicationName().trim().equals("Select"))) {
            entity.setApplicationName(null);
        }
        if (entity.getGxp() != null && (entity.getGxp().trim().isEmpty() || entity.getGxp().trim().equals("Select"))) {
            entity.setGxp(null);
        }
        if (entity.getSquad() != null && (entity.getSquad().trim().isEmpty() || entity.getSquad().trim().equals("Select"))) {
            entity.setSquad(null);
        }
        if (entity.getBusinessOwner() != null && (entity.getBusinessOwner().trim().isEmpty() || entity.getBusinessOwner().trim().equals("Select"))) {
            entity.setBusinessOwner(null);
        }
        if (entity.getSubDomain() != null && (entity.getSubDomain().trim().isEmpty() || entity.getSubDomain().trim().equals("Select"))) {
            entity.setSubDomain(null);
        }
        if (entity.getAppRegion() != null && (entity.getAppRegion().trim().isEmpty() || entity.getAppRegion().trim().equals("Select"))) {
            entity.setAppRegion(null);
        }
        if (entity.getServiceVariant() != null && (entity.getServiceVariant().trim().isEmpty() || entity.getServiceVariant().trim().equals("Select"))) {
            entity.setServiceVariant(null);
        }
        if (entity.getTypeCategory() != null && (entity.getTypeCategory().trim().isEmpty() || entity.getTypeCategory().trim().equals("Select"))) {
            entity.setTypeCategory(null);
        }
        if (entity.getSystemOwner() != null && (entity.getSystemOwner().trim().isEmpty() || entity.getSystemOwner().trim().equals("Select"))) {
            entity.setSystemOwner(null);
        }
        if (entity.getAssignmentGroup() != null && (entity.getAssignmentGroup().trim().isEmpty() || entity.getAssignmentGroup().trim().equals("Select"))) {
            entity.setAssignmentGroup(null);
        }
    }
}
