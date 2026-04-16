package com.example.dictionary.controller;

import com.example.dictionary.entity.ApplicationServerDetail;
import com.example.dictionary.service.ApplicationServerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/application-server-details")
public class ApplicationServerDetailController {
    @Autowired
    private ApplicationServerDetailService service;

    @GetMapping
    public List<ApplicationServerDetail> getAll() {
        return service.findAll();
    }

    @GetMapping("/environment/{env}")
    public List<ApplicationServerDetail> getByEnvironment(@PathVariable String env) {
        return service.findByEnvironment(env);
    }

    @GetMapping("/{id}")
    public Optional<ApplicationServerDetail> getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ApplicationServerDetail create(@RequestBody ApplicationServerDetail entity) {
        return service.save(entity);
    }

    @PostMapping("/bulk")
    public List<ApplicationServerDetail> bulkCreate(@RequestBody Map<String, Object> gridData) {
        return service.processBulkGridData(gridData);
    }

    @PutMapping("/{id}")
    public ApplicationServerDetail update(@PathVariable Long id, @RequestBody ApplicationServerDetail entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
