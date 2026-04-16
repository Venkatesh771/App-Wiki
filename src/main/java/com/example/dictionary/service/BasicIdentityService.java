package com.example.dictionary.service;

import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.repository.BasicIdentityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BasicIdentityService {
    private final BasicIdentityRepository repository;

    public BasicIdentityService(BasicIdentityRepository repository) {
        this.repository = repository;
    }

    public List<BasicIdentity> getAll() {
        return repository.findAll();
    }

    public Optional<BasicIdentity> getById(Long id) {
        return repository.findById(id);
    }

    public BasicIdentity save(BasicIdentity entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
