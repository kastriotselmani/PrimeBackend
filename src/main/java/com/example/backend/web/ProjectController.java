package com.example.backend.web;

import com.example.backend.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService service;
    public ProjectController(ProjectService service) { this.service = service; }

    @GetMapping("/all")
    public Map<String, Object> all(
            Authentication auth,
            @RequestParam(name = "mode", defaultValue = "read") String mode // <-- add this
    ) {
        // Convert ROLE_Admin -> Admin, ROLE_Analyst -> Analyst
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .collect(Collectors.toSet());

        // map query param to service enum
        var access = "write".equalsIgnoreCase(mode)
                ? ProjectService.AccessMode.WRITE
                : ProjectService.AccessMode.READ;

        return Map.of("projects", service.getVisibleTree(roles, access)); // <-- call new overload
    }
}
