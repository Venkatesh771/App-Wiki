package com.example.dictionary.controller;

import com.example.dictionary.entity.DescriptionImpact;
import com.example.dictionary.service.DescriptionImpactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/description-impact")
public class DescriptionImpactController {
    @Autowired
    private DescriptionImpactService service;

    @GetMapping
    public List<DescriptionImpact> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<DescriptionImpact> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public DescriptionImpact create(@RequestBody DescriptionImpact entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public DescriptionImpact update(@PathVariable Long id, @RequestBody DescriptionImpact entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
