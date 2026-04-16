package com.example.dictionary.controller;

import com.example.dictionary.entity.AuthenticationVendor;
import com.example.dictionary.service.AuthenticationVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authentication-vendor")
public class AuthenticationVendorController {
    @Autowired
    private AuthenticationVendorService service;

    @GetMapping
    public List<AuthenticationVendor> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<AuthenticationVendor> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public AuthenticationVendor create(@RequestBody AuthenticationVendor entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public AuthenticationVendor update(@PathVariable Long id, @RequestBody AuthenticationVendor entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
