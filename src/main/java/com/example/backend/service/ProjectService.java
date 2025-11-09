package com.example.backend.service;

import com.example.backend.domain.Project;
import com.example.backend.repo.ProjectStore;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectService {
    public enum AccessMode { READ, WRITE }

    private final ProjectStore store;
    public ProjectService(ProjectStore store){ this.store = store; }

    // === Backward-compatible: defaults to READ view ===
    public List<Project> getVisibleTree(Set<String> userRoles) {
        return getVisibleTree(userRoles, AccessMode.READ);
    }

    // === New: support READ or WRITE views ===
    public List<Project> getVisibleTree(Set<String> userRoles, AccessMode mode) {
        return store.getRoots().stream()
                .map(p -> filterNodeByMode(p, userRoles, mode))
                .filter(Objects::nonNull)
                .toList();
    }

    // --- Filtering by mode (READ/WRITE) ---
    private Project filterNodeByMode(Project p, Set<String> userRoles, AccessMode mode) {
        boolean allowed = (mode == AccessMode.WRITE) ? canWrite(p, userRoles) : canRead(p, userRoles);
        if (!allowed) return null;

        var copy = Project.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription()).createdAt(p.getCreatedAt())
                .items(p.getItems())
                .allowedReadRoles(p.getAllowedReadRoles())
                .allowedWriteRoles(p.getAllowedWriteRoles())
                .children(new ArrayList<>())
                .build();

        for (var child : p.getChildren()) {
            var pruned = filterNodeByMode(child, userRoles, mode);
            if (pruned != null) copy.getChildren().add(pruned);
        }
        return copy;
    }

    // --- Original READ filtering (kept for clarity/reuse) ---
    private Project filterNode(Project p, Set<String> userRoles) {
        if (!canRead(p, userRoles)) return null;
        var copy = Project.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription()).createdAt(p.getCreatedAt())
                .items(p.getItems())
                .allowedReadRoles(p.getAllowedReadRoles())
                .allowedWriteRoles(p.getAllowedWriteRoles())
                .children(new ArrayList<>())
                .build();
        for (var child : p.getChildren()) {
            var pruned = filterNode(child, userRoles);
            if (pruned != null) copy.getChildren().add(pruned);
        }
        return copy;
    }

    // --- Access checks ---
    private boolean canRead(Project p, Set<String> userRoles) {
        var read = new HashSet<>(p.getAllowedReadRoles());
        if (read.contains("All")) return true;
        read.addAll(p.getAllowedWriteRoles()); // write implies read
        for (var r : userRoles) if (read.contains(r)) return true;
        return false;
    }

    private boolean canWrite(Project p, Set<String> userRoles) {
        var write = p.getAllowedWriteRoles();
        for (var r : userRoles) if (write.contains(r)) return true;
        return false;
    }
}
