package com.example.dictionary.controller;

import com.example.dictionary.entity.ResourceContacts;
import com.example.dictionary.service.ResourceContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resource-contacts")
public class ResourceContactsController {
    @Autowired
    private ResourceContactsService service;

    @GetMapping
    public List<ResourceContacts> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ResourceContacts> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResourceContacts create(@RequestBody ResourceContacts entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ResourceContacts update(@PathVariable Long id, @RequestBody ResourceContacts entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
