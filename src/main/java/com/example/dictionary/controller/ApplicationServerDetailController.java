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
        return service.save(entity);    }    @PostMapping("/bulk")
    @SuppressWarnings("unchecked")
    public List<ApplicationServerDetail> bulkCreate(@RequestBody Map<String, Object> requestData) {
        // Extract basicIdentityId, beatId, and gridData from request
        Long basicIdentityId = null;
        String beatId = null;
        Map<String, Object> gridData = null;
        
        if (requestData.containsKey("basicIdentityId")) {
            Object idObj = requestData.get("basicIdentityId");
            basicIdentityId = idObj instanceof Number ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
        }
        
        if (requestData.containsKey("beatId")) {
            Object beatIdObj = requestData.get("beatId");
            beatId = beatIdObj != null ? beatIdObj.toString() : null;
        }
        
        if (requestData.containsKey("gridData")) {
            Object gridDataObj = requestData.get("gridData");
            if (gridDataObj instanceof Map) {
                gridData = (Map<String, Object>) gridDataObj;
            } else {
                gridData = new java.util.HashMap<>();
            }
        } else {
            // If gridData is not nested, use the entire requestData
            gridData = new java.util.HashMap<>(requestData);
            gridData.remove("basicIdentityId");
            gridData.remove("beatId");
        }
        
        return service.processBulkGridData(gridData, basicIdentityId, beatId);
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
