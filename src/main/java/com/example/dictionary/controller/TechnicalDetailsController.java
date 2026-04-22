package com.example.dictionary.controller;

import com.example.dictionary.entity.TechnicalDetails;
import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.model.TechnicalDetailsDTO;
import com.example.dictionary.service.TechnicalDetailsService;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/technical-details")
public class TechnicalDetailsController {
    @Autowired
    private TechnicalDetailsService service;
    
    @Autowired
    private BasicIdentityRepository basicIdentityRepository;

    @GetMapping
    public List<TechnicalDetails> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<TechnicalDetails> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public TechnicalDetails create(@RequestBody TechnicalDetailsDTO dto) {
        TechnicalDetails entity = new TechnicalDetails();
        entity.setExternalInternal(dto.getExternalInternal());
        entity.setGithubRepos(dto.getGithubRepos());
        entity.setKafkaTopic(dto.getKafkaTopic());
        entity.setAkanaMulesoft(dto.getAkanaMusoft());
        entity.setAppUrlsDevTest(dto.getAppUrlsDevTest());
        entity.setAzureClientIds(dto.getAzureClientIds());
        entity.setLbVipDetails(dto.getLbVipDetails());
        entity.setUpstreamSystem(dto.getUpstreamSystem());
        
        if (dto.getBasicIdentityId() != null) {
            Optional<BasicIdentity> basicIdentity = basicIdentityRepository.findById(dto.getBasicIdentityId());
            if (basicIdentity.isPresent()) {
                entity.setBasicIdentity(basicIdentity.get());
            }
        }
        
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public TechnicalDetails update(@PathVariable Long id, @RequestBody TechnicalDetailsDTO dto) {
        Optional<TechnicalDetails> existing = service.findById(id);
        if (existing.isPresent()) {
            TechnicalDetails entity = existing.get();
            entity.setExternalInternal(dto.getExternalInternal());
            entity.setGithubRepos(dto.getGithubRepos());
            entity.setKafkaTopic(dto.getKafkaTopic());
            entity.setAkanaMulesoft(dto.getAkanaMusoft());
            entity.setAppUrlsDevTest(dto.getAppUrlsDevTest());
            entity.setAzureClientIds(dto.getAzureClientIds());
            entity.setLbVipDetails(dto.getLbVipDetails());
            entity.setUpstreamSystem(dto.getUpstreamSystem());
            
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
