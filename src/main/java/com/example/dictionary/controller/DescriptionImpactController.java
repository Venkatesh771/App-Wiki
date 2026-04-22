package com.example.dictionary.controller;

import com.example.dictionary.entity.DescriptionImpact;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.model.DescriptionImpactDTO;
import com.example.dictionary.service.DescriptionImpactService;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/description-impact")
public class DescriptionImpactController {
    @Autowired
    private DescriptionImpactService service;
    
    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

    @GetMapping
    public List<DescriptionImpact> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<DescriptionImpact> getById(@PathVariable Long id) {
        return service.findById(id);
    }    @PostMapping
    public DescriptionImpact create(@RequestBody DescriptionImpactDTO dto) {
        System.out.println("=== DescriptionImpact Controller ===");
        System.out.println("Received DTO basicIdentityId: " + dto.getBasicIdentityId());
        System.out.println("Received DTO applicationDescription: " + dto.getApplicationDescription());
        
        DescriptionImpact entity = new DescriptionImpact();
        entity.setApplicationDescription(dto.getApplicationDescription());
        entity.setBusinessImpactDescription(dto.getBusinessImpactDescription());
        entity.setFinancialImpact(dto.getFinancialImpact());
        entity.setUserAccessReview(dto.getUserAccessReview());
        
        // Set the relationship
        if (dto.getBasicIdentityId() != null) {
            System.out.println("Looking up BasicIdentity with ID: " + dto.getBasicIdentityId());
            Optional<BasicIdentity> basicIdentity = basicIdentityRepository.findById(dto.getBasicIdentityId());
            if (basicIdentity.isPresent()) {
                entity.setBasicIdentity(basicIdentity.get());
                System.out.println("✅ BasicIdentity found and set");
            } else {
                System.out.println("❌ BasicIdentity NOT found");
            }
        } else {
            System.out.println("❌ basicIdentityId is NULL in DTO");
        }
        
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public DescriptionImpact update(@PathVariable Long id, @RequestBody DescriptionImpactDTO dto) {
        Optional<DescriptionImpact> existing = service.findById(id);
        if (existing.isPresent()) {
            DescriptionImpact entity = existing.get();
            entity.setApplicationDescription(dto.getApplicationDescription());
            entity.setBusinessImpactDescription(dto.getBusinessImpactDescription());
            entity.setFinancialImpact(dto.getFinancialImpact());
            entity.setUserAccessReview(dto.getUserAccessReview());
            
            if (dto.getBasicIdentityId() != null) {
                Optional<BasicIdentity> basicIdentity = basicIdentityRepository.findById(dto.getBasicIdentityId());
                if (basicIdentity.isPresent()) {
                    entity.setBasicIdentity(basicIdentity.get());
                }
            }
            
            return service.save(entity);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
