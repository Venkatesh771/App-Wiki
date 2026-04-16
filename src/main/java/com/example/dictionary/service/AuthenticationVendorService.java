package com.example.dictionary.service;

import com.example.dictionary.entity.AuthenticationVendor;
import com.example.dictionary.repository.AuthenticationVendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationVendorService {
    @Autowired
    private AuthenticationVendorRepository repository;

    public List<AuthenticationVendor> findAll() {
        return repository.findAll();
    }

    public Optional<AuthenticationVendor> findById(Long id) {
        return repository.findById(id);
    }

    public AuthenticationVendor save(AuthenticationVendor entity) {
        return repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
