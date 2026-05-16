package com.example.dictionary.controller;

import com.example.dictionary.entity.ApplicationServerDetail;
import com.example.dictionary.service.ApplicationServerDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApplicationServerDetail> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApplicationServerDetail create(@RequestBody ApplicationServerDetail entity,
                                           @RequestParam(required = false) Long basicIdentityId) {
        return basicIdentityId != null ? service.save(entity, basicIdentityId) : service.save(entity);
    }    @PostMapping("/bulk")
    @SuppressWarnings("unchecked")
    public List<ApplicationServerDetail> bulkCreate(@RequestBody Map<String, Object> requestData) {

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

            gridData = new java.util.HashMap<>(requestData);
            gridData.remove("basicIdentityId");
            gridData.remove("beatId");
        }

        return service.processBulkGridData(gridData, basicIdentityId, beatId);
    }

    @PutMapping("/{id}")
    public ApplicationServerDetail update(@PathVariable Long id, @RequestBody ApplicationServerDetail entity) {
        Optional<ApplicationServerDetail> existing = service.findById(id);
        if (existing.isPresent()) {
            ApplicationServerDetail e = existing.get();
            e.setDeployedServer(entity.getDeployedServer());
            e.setServerName(entity.getServerName());
            e.setServerOsVersion(entity.getServerOsVersion());
            e.setDomain(entity.getDomain());
            e.setCluster(entity.getCluster());
            e.setServiceName(entity.getServiceName());
            e.setIpAddress(entity.getIpAddress());
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
