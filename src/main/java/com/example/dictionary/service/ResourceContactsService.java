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
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
