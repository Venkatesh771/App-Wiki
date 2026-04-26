package com.example.dictionary.controller;

import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.service.BasicIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/api/basic-identity")
public class BasicIdentityController {
    @Autowired
    private BasicIdentityService service;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(
            @RequestParam(required = false) String beatId,
            @RequestParam(required = false) String applicationName,
            @RequestParam(required = false) Long excludeId) {
        boolean beatIdExists;
        boolean appNameExists;
        if (excludeId != null) {
            beatIdExists = beatId != null && !beatId.isBlank() && service.existsByBeatIdExcludingId(beatId, excludeId);
            appNameExists = applicationName != null && !applicationName.isBlank() && service.existsByApplicationNameExcludingId(applicationName, excludeId);
        } else {
            beatIdExists = beatId != null && !beatId.isBlank() && service.existsByBeatId(beatId);
            appNameExists = applicationName != null && !applicationName.isBlank() && service.existsByApplicationName(applicationName);
        }
        return ResponseEntity.ok(Map.of("beatIdExists", beatIdExists, "applicationNameExists", appNameExists));
    }

    @GetMapping
    public List<BasicIdentity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicIdentity> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BasicIdentity entity) {
        if (entity.getBeatId() != null && !entity.getBeatId().isBlank()) {
            if (service.existsByBeatId(entity.getBeatId())) {
                return ResponseEntity.status(409)
                    .body(Map.of("error", "Application Already Exists! Beat ID \"" + entity.getBeatId() + "\" is already registered. Please use a different Beat ID."));
            }
        }
        if (entity.getApplicationName() != null && !entity.getApplicationName().isBlank()) {
            if (service.existsByApplicationName(entity.getApplicationName())) {
                return ResponseEntity.status(409)
                    .body(Map.of("error", "Application Already Exists! \"" + entity.getApplicationName() + "\" is already registered. Please use a different Application Name."));
            }
        }
        try {
            return ResponseEntity.ok(service.save(entity));
        } catch (DataIntegrityViolationException ex) {
            String msg = "A duplicate Beat ID or Application Name was detected. Please use unique values.";
            if (ex.getMessage() != null && ex.getMessage().contains("beat_id")) {
                msg = "Beat ID \"" + entity.getBeatId() + "\" already exists. Please use a different Beat ID.";
            } else if (ex.getMessage() != null && ex.getMessage().contains("application_name")) {
                msg = "Application Name \"" + entity.getApplicationName() + "\" already exists. Please use a different Application Name.";
            }
            return ResponseEntity.status(409).body(Map.of("error", msg));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BasicIdentity entity) {
        Optional<BasicIdentity> existing = service.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (entity.getBeatId() != null && !entity.getBeatId().isBlank()) {
            if (service.existsByBeatIdExcludingId(entity.getBeatId(), id)) {
                return ResponseEntity.status(409)
                    .body(Map.of("error", "Application Already Exists! Beat ID \"" + entity.getBeatId() + "\" is already used by another application."));
            }
        }
        if (entity.getApplicationName() != null && !entity.getApplicationName().isBlank()) {
            if (service.existsByApplicationNameExcludingId(entity.getApplicationName(), id)) {
                return ResponseEntity.status(409)
                    .body(Map.of("error", "Application Already Exists! \"" + entity.getApplicationName() + "\" is already used by another application."));
            }
        }
        try {
            BasicIdentity e = existing.get();
            e.setBeatId(entity.getBeatId());
            e.setApplicationName(entity.getApplicationName());
            e.setGxp(entity.getGxp());
            e.setSquad(entity.getSquad());
            e.setBusinessOwner(entity.getBusinessOwner());
            e.setSubDomain(entity.getSubDomain());
            e.setAppRegion(entity.getAppRegion());
            e.setServiceVariant(entity.getServiceVariant());
            e.setTypeCategory(entity.getTypeCategory());
            e.setSystemOwner(entity.getSystemOwner());
            e.setAssignmentGroup(entity.getAssignmentGroup());
            return ResponseEntity.ok(service.save(e));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409)
                .body(Map.of("error", "Application Already Exists! Beat ID or Application Name is already used by another application."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        return service.deactivate(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
