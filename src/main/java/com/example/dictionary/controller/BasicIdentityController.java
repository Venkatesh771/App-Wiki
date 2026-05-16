package com.example.dictionary.controller;

import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.model.User;
import com.example.dictionary.service.ActivityLogService;
import com.example.dictionary.service.AuthenticationService;
import com.example.dictionary.service.BasicIdentityService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ActivityLogService activityLogService;

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
    public ResponseEntity<?> create(@RequestBody BasicIdentity entity, HttpSession session) {
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
            BasicIdentity saved = service.save(entity);
            User actor = (User) session.getAttribute("user");
            if (actor != null) {
                activityLogService.recordAppAdd(actor.getCwid(), actor.getUsername(), actor.getRole(),
                        saved.getBeatId(), saved.getApplicationName());
            }
            return ResponseEntity.ok(saved);
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BasicIdentity entity, HttpSession session) {
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

            BasicIdentity beforeSnap = snapshot(e);
            String oldGroup = e.getAssignmentGroup();
            String newGroup = entity.getAssignmentGroup();
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
            e.setAssignmentGroup(newGroup);
            BasicIdentity saved = service.save(e);
            String[] diff = diffApp(beforeSnap, saved);
            User actor = (User) session.getAttribute("user");
            if (actor != null && !diff[0].isEmpty()) {
                activityLogService.recordAppEdit(actor.getCwid(), actor.getUsername(),
                        saved.getBeatId(), saved.getApplicationName(),
                        "Application", diff[0], diff[1]);
            }

            if (oldGroup != null && !oldGroup.isBlank()
                    && newGroup != null && !newGroup.isBlank()
                    && !oldGroup.equals(newGroup)) {
                List<String> remaining = service.getDistinctAssignmentGroups();
                if (remaining == null || !remaining.contains(oldGroup)) {
                    authenticationService.renameGroupInAllFilters(oldGroup, newGroup);
                }
            }
            return ResponseEntity.ok(saved);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409)
                .body(Map.of("error", "Application Already Exists! Beat ID or Application Name is already used by another application."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        Optional<BasicIdentity> existing = service.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BasicIdentity app = existing.get();
        service.delete(id);
        User actor = (User) session.getAttribute("user");
        if (actor != null) {
            activityLogService.recordAppDelete(actor.getCwid(), actor.getUsername(),
                    app.getBeatId(), app.getApplicationName(), "Application", summarizeApp(app));
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id, HttpSession session) {
        Optional<BasicIdentity> existing = service.getById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        BasicIdentity app = existing.get();
        boolean ok = service.deactivate(id);
        if (ok) {
            User actor = (User) session.getAttribute("user");
            if (actor != null) {
                activityLogService.recordAppDelete(actor.getCwid(), actor.getUsername(),
                        app.getBeatId(), app.getApplicationName(), "Application", summarizeApp(app));
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private static String summarizeApp(BasicIdentity a) {
        if (a == null) return "";
        StringBuilder sb = new StringBuilder();
        appendField(sb, "Beat ID", a.getBeatId());
        appendField(sb, "Name", a.getApplicationName());
        appendField(sb, "Squad", a.getSquad());
        appendField(sb, "Assignment Group", a.getAssignmentGroup());
        appendField(sb, "System Owner", a.getSystemOwner());
        appendField(sb, "Business Owner", a.getBusinessOwner());
        appendField(sb, "Sub Domain", a.getSubDomain());
        appendField(sb, "Region", a.getAppRegion());
        appendField(sb, "Service Variant", a.getServiceVariant());
        appendField(sb, "Type", a.getTypeCategory());
        appendField(sb, "GxP", a.getGxp());
        return sb.toString();
    }

    private static void appendField(StringBuilder sb, String label, String value) {
        if (value == null || value.isBlank()) return;
        if (sb.length() > 0) sb.append("; ");
        sb.append(label).append(": ").append(value);
    }

    private static BasicIdentity snapshot(BasicIdentity src) {
        BasicIdentity s = new BasicIdentity();
        s.setBeatId(src.getBeatId());
        s.setApplicationName(src.getApplicationName());
        s.setGxp(src.getGxp());
        s.setSquad(src.getSquad());
        s.setBusinessOwner(src.getBusinessOwner());
        s.setSubDomain(src.getSubDomain());
        s.setAppRegion(src.getAppRegion());
        s.setServiceVariant(src.getServiceVariant());
        s.setTypeCategory(src.getTypeCategory());
        s.setSystemOwner(src.getSystemOwner());
        s.setAssignmentGroup(src.getAssignmentGroup());
        return s;
    }

    private static String[] diffApp(BasicIdentity before, BasicIdentity after) {
        StringBuilder oldSb = new StringBuilder();
        StringBuilder newSb = new StringBuilder();
        addDiff(oldSb, newSb, "Beat ID", before.getBeatId(), after.getBeatId());
        addDiff(oldSb, newSb, "Name", before.getApplicationName(), after.getApplicationName());
        addDiff(oldSb, newSb, "Squad", before.getSquad(), after.getSquad());
        addDiff(oldSb, newSb, "Assignment Group", before.getAssignmentGroup(), after.getAssignmentGroup());
        addDiff(oldSb, newSb, "System Owner", before.getSystemOwner(), after.getSystemOwner());
        addDiff(oldSb, newSb, "Business Owner", before.getBusinessOwner(), after.getBusinessOwner());
        addDiff(oldSb, newSb, "Sub Domain", before.getSubDomain(), after.getSubDomain());
        addDiff(oldSb, newSb, "Region", before.getAppRegion(), after.getAppRegion());
        addDiff(oldSb, newSb, "Service Variant", before.getServiceVariant(), after.getServiceVariant());
        addDiff(oldSb, newSb, "Type", before.getTypeCategory(), after.getTypeCategory());
        addDiff(oldSb, newSb, "GxP", before.getGxp(), after.getGxp());
        return new String[] { oldSb.toString(), newSb.toString() };
    }

    private static void addDiff(StringBuilder oldSb, StringBuilder newSb, String label, String oldV, String newV) {
        String a = oldV == null ? "" : oldV;
        String b = newV == null ? "" : newV;
        if (a.equals(b)) return;
        if (oldSb.length() > 0) oldSb.append("; ");
        if (newSb.length() > 0) newSb.append("; ");
        oldSb.append(label).append(": ").append(a.isEmpty() ? "—" : a);
        newSb.append(label).append(": ").append(b.isEmpty() ? "—" : b);
    }
}
