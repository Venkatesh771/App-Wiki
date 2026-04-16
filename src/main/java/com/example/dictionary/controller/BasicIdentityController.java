package com.example.dictionary.controller;

import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.service.BasicIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/basic-identity")
public class BasicIdentityController {
    @Autowired
    private BasicIdentityService service;

    @GetMapping
    public List<BasicIdentity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<BasicIdentity> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public BasicIdentity create(@RequestBody BasicIdentity entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public BasicIdentity update(@PathVariable Long id, @RequestBody BasicIdentity entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
