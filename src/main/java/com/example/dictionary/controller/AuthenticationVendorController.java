package com.example.dictionary.controller;

import com.example.dictionary.entity.AuthenticationVendor;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.model.AuthenticationVendorDTO;
import com.example.dictionary.service.AuthenticationVendorService;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authentication-vendor")
public class AuthenticationVendorController {
    @Autowired
    private AuthenticationVendorService service;
    
    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

    @GetMapping
    public List<AuthenticationVendor> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<AuthenticationVendor> getById(@PathVariable Long id) {
        return service.findById(id);
    }    @PostMapping
    public AuthenticationVendor create(@RequestBody AuthenticationVendorDTO dto) {
        AuthenticationVendor entity = new AuthenticationVendor();
        entity.setVendorName(dto.getVendorName());
        entity.setAuthenticationType(dto.getAuthenticationType());
        entity.setAuthorizationType(dto.getAuthorizationType());
        entity.setVendorApp(dto.getVendorApp());
        entity.setVendorContactDetails(dto.getVendorContactDetails());
        entity.setVendorSupportExist(dto.getVendorSupportExist());
        entity.setVendorTicketingUrl(dto.getVendorTicketingUrl());
        entity.setAppDecommissioned(dto.getAppDecommissioned());
        entity.setAppHostedIn(dto.getAppHostedIn());
        entity.setAuthDetailsRouting(dto.getAuthDetailsRouting());
        
        if (dto.getBasicIdentityId() != null) {
            Optional<BasicIdentity> basicIdentity = basicIdentityRepository.findById(dto.getBasicIdentityId());
            if (basicIdentity.isPresent()) {
                entity.setBasicIdentity(basicIdentity.get());
            }
        }
        
        return service.save(entity);
    }    @PutMapping("/{id}")
    public AuthenticationVendor update(@PathVariable Long id, @RequestBody AuthenticationVendorDTO dto) {
        Optional<AuthenticationVendor> existing = service.findById(id);
        if (existing.isPresent()) {
            AuthenticationVendor entity = existing.get();
            entity.setVendorName(dto.getVendorName());
            entity.setAuthenticationType(dto.getAuthenticationType());
            entity.setAuthorizationType(dto.getAuthorizationType());
            entity.setVendorApp(dto.getVendorApp());
            entity.setVendorContactDetails(dto.getVendorContactDetails());
            entity.setVendorSupportExist(dto.getVendorSupportExist());
            entity.setVendorTicketingUrl(dto.getVendorTicketingUrl());
            entity.setAppDecommissioned(dto.getAppDecommissioned());
            entity.setAppHostedIn(dto.getAppHostedIn());
            entity.setAuthDetailsRouting(dto.getAuthDetailsRouting());
            
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
