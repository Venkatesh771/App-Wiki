package com.example.dictionary.controller;

import com.example.dictionary.entity.DatabaseServerDetail;
import com.example.dictionary.service.DatabaseServerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DatabaseServerDetail> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DatabaseServerDetail create(@RequestBody DatabaseServerDetail entity,
                                        @RequestParam(required = false) Long basicIdentityId) {
        return basicIdentityId != null ? service.save(entity, basicIdentityId) : service.save(entity);
    }

    @PostMapping("/bulk")
    @SuppressWarnings("unchecked")
    public List<DatabaseServerDetail> bulkCreate(@RequestBody Map<String, Object> requestData) {
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
    public DatabaseServerDetail update(@PathVariable Long id, @RequestBody DatabaseServerDetail entity) {
        Optional<DatabaseServerDetail> existing = service.findById(id);
        if (existing.isPresent()) {
            DatabaseServerDetail e = existing.get();
            e.setDatabaseType(entity.getDatabaseType());
            e.setDatabaseVersion(entity.getDatabaseVersion());
            e.setDatabaseHostingType(entity.getDatabaseHostingType());
            e.setDatabaseName(entity.getDatabaseName());
            e.setAccountName(entity.getAccountName());
            e.setHostName(entity.getHostName());
            e.setServiceName(entity.getServiceName());
            e.setPort(entity.getPort());
            e.setAccountId(entity.getAccountId());
            e.setIp(entity.getIp());
            return service.save(e);
        }
        entity.setId(id);
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        return service.deactivate(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
