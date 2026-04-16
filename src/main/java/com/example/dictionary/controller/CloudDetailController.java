package com.example.dictionary.controller;

import com.example.dictionary.entity.CloudDetail;
import com.example.dictionary.service.CloudDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/cloud-details")
public class CloudDetailController {
    @Autowired
    private CloudDetailService service;

    @GetMapping
    public List<CloudDetail> getAll() {
        return service.findAll();
    }

    @GetMapping("/environment/{env}")
    public List<CloudDetail> getByEnvironment(@PathVariable String env) {
        return service.findByEnvironment(env);
    }

    @GetMapping("/{id}")
    public Optional<CloudDetail> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public CloudDetail create(@RequestBody CloudDetail entity) {
        return service.save(entity);
    }

    @PostMapping("/bulk")
    public List<CloudDetail> bulkCreate(@RequestBody Map<String, Object> gridData) {
        return service.processBulkGridData(gridData);
    }

    @PutMapping("/{id}")
    public CloudDetail update(@PathVariable Long id, @RequestBody CloudDetail entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
