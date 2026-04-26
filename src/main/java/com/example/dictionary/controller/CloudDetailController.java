package com.example.dictionary.controller;

import com.example.dictionary.entity.CloudDetail;
import com.example.dictionary.service.CloudDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CloudDetail> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CloudDetail create(@RequestBody CloudDetail entity,
                               @RequestParam(required = false) Long basicIdentityId) {
        return basicIdentityId != null ? service.save(entity, basicIdentityId) : service.save(entity);
    }    @PostMapping("/bulk")
    @SuppressWarnings("unchecked")
    public List<CloudDetail> bulkCreate(@RequestBody Map<String, Object> requestData) {
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
    public CloudDetail update(@PathVariable Long id, @RequestBody CloudDetail entity) {
        Optional<CloudDetail> existing = service.findById(id);
        if (existing.isPresent()) {
            CloudDetail e = existing.get();
            e.setAccountId(entity.getAccountId());
            e.setHostType(entity.getHostType());
            e.setServiceName(entity.getServiceName());
            e.setLambdaNames(entity.getLambdaNames());
            e.setS3Bucket(entity.getS3Bucket());
            e.setSqsNames(entity.getSqsNames());
            e.setIamUser(entity.getIamUser());
            e.setComments(entity.getComments());
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
