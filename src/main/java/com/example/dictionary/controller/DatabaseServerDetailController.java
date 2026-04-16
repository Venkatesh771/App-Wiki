package com.example.dictionary.controller;

import com.example.dictionary.entity.DatabaseServerDetail;
import com.example.dictionary.service.DatabaseServerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/database-server-details")
public class DatabaseServerDetailController {
    @Autowired
    private DatabaseServerDetailService service;

    @GetMapping
    public List<DatabaseServerDetail> getAll() {
        return service.findAll();
    }

    @GetMapping("/environment/{env}")
    public List<DatabaseServerDetail> getByEnvironment(@PathVariable String env) {
        return service.findByEnvironment(env);
    }

    @GetMapping("/{id}")
    public Optional<DatabaseServerDetail> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public DatabaseServerDetail create(@RequestBody DatabaseServerDetail entity) {
        return service.save(entity);
    }

    @PostMapping("/bulk")
    public List<DatabaseServerDetail> bulkCreate(@RequestBody Map<String, Object> gridData) {
        return service.processBulkGridData(gridData);
    }

    @PutMapping("/{id}")
    public DatabaseServerDetail update(@PathVariable Long id, @RequestBody DatabaseServerDetail entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
