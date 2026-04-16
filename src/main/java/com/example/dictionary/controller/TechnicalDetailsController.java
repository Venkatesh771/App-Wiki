package com.example.dictionary.controller;

import com.example.dictionary.entity.TechnicalDetails;
import com.example.dictionary.service.TechnicalDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/technical-details")
public class TechnicalDetailsController {
    @Autowired
    private TechnicalDetailsService service;

    @GetMapping
    public List<TechnicalDetails> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<TechnicalDetails> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public TechnicalDetails create(@RequestBody TechnicalDetails entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public TechnicalDetails update(@PathVariable Long id, @RequestBody TechnicalDetails entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
