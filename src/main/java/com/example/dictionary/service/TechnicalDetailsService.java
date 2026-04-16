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
    }

    public TechnicalDetails save(TechnicalDetails entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
