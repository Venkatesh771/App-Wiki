package com.example.dictionary.controller;

import com.example.dictionary.service.DropdownOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dropdown-options")
public class DropdownOptionController {

    @Autowired
    private DropdownOptionService service;

    @GetMapping("/{fieldName}")
    public List<String> getOptions(@PathVariable String fieldName) {
        return service.getValues(fieldName);
    }

    @PostMapping
    public ResponseEntity<?> addOption(@RequestBody Map<String, String> body) {
        String fieldName = body.get("fieldName");
        String value = body.get("value");
        if (fieldName == null || fieldName.isBlank() || value == null || value.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        boolean saved = service.save(fieldName.trim(), value.trim());
        if (!saved) {
            return ResponseEntity.status(409)
                .body(Map.of("error", "\"" + value.trim() + "\" already exists in this dropdown."));
        }
        return ResponseEntity.ok().build();
    }
}
