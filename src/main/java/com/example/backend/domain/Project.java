package com.example.backend.domain;
import lombok.*; import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {
    private String id, name, description, createdAt;

    @Builder.Default private List<Project> children = new ArrayList<>();
    @Builder.Default private List<ProjectItem> items = new ArrayList<>();

    // role-based access stored with the data (write ⇒ read handled in service)
    @Builder.Default private Set<String> allowedReadRoles  = Set.of();   // e.g. ["Admin","Analyst"] or ["All"]
    @Builder.Default private Set<String> allowedWriteRoles = Set.of();   // e.g. ["Admin"]
}
