package com.example.backend.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // Only users with ROLE_Admin (i.e., "Admin" in your JWT roles) can access
    @GetMapping("/ping")
    @PreAuthorize("hasRole('Admin')")
    public Map<String, Object> ping() {
        return Map.of("ok", true, "area", "admin", "message", "pong");
    }
}
