package com.example.dictionary.controller;

import com.example.dictionary.entity.Group;
import com.example.dictionary.model.User;
import com.example.dictionary.service.ActivityLogService;
import com.example.dictionary.service.GroupService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService service;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public List<Map<String, Object>> list() {
        return service.listWithCounts();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String, String> body, HttpSession session) {
        String l15 = body.get("l15Name");
        String l2 = body.get("l2Name");
        if (l15 == null || l15.isBlank() || l2 == null || l2.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Both L1.5 and L2 names are required."));
        }
        l15 = l15.trim();
        l2 = l2.trim();
        if (service.exists(l15, l2)) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Group with L1.5 \"" + l15 + "\" and L2 \"" + l2 + "\" already exists."));
        }
        Group saved = service.add(l15, l2);
        User actor = (User) session.getAttribute("user");
        if (actor != null) {
            activityLogService.recordGroupAdd(actor.getCwid(), actor.getUsername(),
                    saved.getL15Name(), saved.getL2Name());
        }
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, String> body, HttpSession session) {
        String l15 = body.get("l15Name");
        String l2 = body.get("l2Name");
        if (l15 == null || l15.isBlank() || l2 == null || l2.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Both L1.5 and L2 names are required."));
        }
        l15 = l15.trim();
        l2 = l2.trim();
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Group existing = service.findById(id).get();
        String oldL15 = existing.getL15Name();
        String oldL2 = existing.getL2Name();
        boolean pairChanged = !l15.equals(oldL15) || !l2.equals(oldL2);
        if (pairChanged && service.exists(l15, l2)) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Group with L1.5 \"" + l15 + "\" and L2 \"" + l2 + "\" already exists."));
        }
        Group saved = service.update(id, l15, l2);
        if (pairChanged) {
            User actor = (User) session.getAttribute("user");
            if (actor != null) {
                String[] diff = groupDiff(oldL15, oldL2, saved.getL15Name(), saved.getL2Name());
                activityLogService.recordGroupEdit(actor.getCwid(), actor.getUsername(),
                        "Group", diff[0], diff[1]);
            }
        }
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpSession session) {
        var existing = service.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        Group g = existing.get();
        if (!service.delete(id)) return ResponseEntity.notFound().build();
        User actor = (User) session.getAttribute("user");
        if (actor != null) {
            String oldSummary = "L1.5: " + g.getL15Name() + "; L2: " + g.getL2Name();
            activityLogService.recordGroupDelete(actor.getCwid(), actor.getUsername(), "Group", oldSummary);
        }
        return ResponseEntity.noContent().build();
    }

    private static String[] groupDiff(String oldL15, String oldL2, String newL15, String newL2) {
        StringBuilder oldSb = new StringBuilder();
        StringBuilder newSb = new StringBuilder();
        if (!safeEq(oldL15, newL15)) {
            append(oldSb, "L1.5: " + (oldL15 == null || oldL15.isEmpty() ? "—" : oldL15));
            append(newSb, "L1.5: " + (newL15 == null || newL15.isEmpty() ? "—" : newL15));
        }
        if (!safeEq(oldL2, newL2)) {
            append(oldSb, "L2: " + (oldL2 == null || oldL2.isEmpty() ? "—" : oldL2));
            append(newSb, "L2: " + (newL2 == null || newL2.isEmpty() ? "—" : newL2));
        }
        return new String[] { oldSb.toString(), newSb.toString() };
    }

    private static void append(StringBuilder sb, String s) {
        if (sb.length() > 0) sb.append("; ");
        sb.append(s);
    }

    private static boolean safeEq(String a, String b) {
        return (a == null ? "" : a).equals(b == null ? "" : b);
    }
}
