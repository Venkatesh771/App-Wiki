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
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
