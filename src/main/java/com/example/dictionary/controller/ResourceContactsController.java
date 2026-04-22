package com.example.dictionary.controller;

import com.example.dictionary.entity.ResourceContacts;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.model.ResourceContactsDTO;
import com.example.dictionary.service.ResourceContactsService;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resource-contacts")
public class ResourceContactsController {
    @Autowired
    private ResourceContactsService service;
    
    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

    @GetMapping
    public List<ResourceContacts> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ResourceContacts> getById(@PathVariable Long id) {
        return service.findById(id);
    }    @PostMapping
    public ResourceContacts create(@RequestBody ResourceContactsDTO dto) {
        ResourceContacts entity = new ResourceContacts();
        entity.setOnshorePrimary(dto.getOnshorePrimary());
        entity.setOnshoreSecondary(dto.getOnshoreSecondary());
        entity.setOnshoreTertiary(dto.getOnshoreTertiary());
        entity.setOffshorePrimary(dto.getOffshorePrimary());
        entity.setOffshoreSecondary(dto.getOffshoreSecondary());
        entity.setOffshoreTertiary(dto.getOffshoreTertiary());
        
        if (dto.getBasicIdentityId() != null) {
            Optional<BasicIdentity> basicIdentity = basicIdentityRepository.findById(dto.getBasicIdentityId());
            if (basicIdentity.isPresent()) {
                entity.setBasicIdentity(basicIdentity.get());
            }
        }
        
        return service.save(entity);
    }    @PutMapping("/{id}")
    public ResourceContacts update(@PathVariable Long id, @RequestBody ResourceContactsDTO dto) {
        Optional<ResourceContacts> existing = service.findById(id);
        if (existing.isPresent()) {
            ResourceContacts entity = existing.get();
            entity.setOnshorePrimary(dto.getOnshorePrimary());
            entity.setOnshoreSecondary(dto.getOnshoreSecondary());
            entity.setOnshoreTertiary(dto.getOnshoreTertiary());
            entity.setOffshorePrimary(dto.getOffshorePrimary());
            entity.setOffshoreSecondary(dto.getOffshoreSecondary());
            entity.setOffshoreTertiary(dto.getOffshoreTertiary());
            
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
