package com.example.backend.repo;

import com.example.backend.domain.AttachmentItem;
import com.example.backend.domain.NoteItem;
import com.example.backend.domain.Priority;
import com.example.backend.domain.Project;
import com.example.backend.domain.TaskItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ProjectStore {
    private final List<Project> roots;

    public ProjectStore() {
        // Root: Retail (Admin + Analyst can read; Admin can write)
        var retail = Project.builder()
                .id("p1")
                .name("Retail")
                .description("Retail root")
                .createdAt("2024-01-10")
                .allowedReadRoles(Set.of("Admin", "Analyst"))
                .allowedWriteRoles(Set.of("Admin"))
                .children(new ArrayList<>())
                .build();

        // Child: Stock (anyone can read; Admin & Analyst can write)
        var stock = Project.builder()
                .id("p1-1")
                .name("Stock")
                .description("Stock subtree")
                .createdAt("2024-01-12")
                .allowedReadRoles(Set.of("All"))                 // everyone can read
                .allowedWriteRoles(Set.of("Admin", "Analyst"))   // only these can write
                .children(new ArrayList<>())
                .build();

        retail.getChildren().add(stock);

        // Root: Marketing (Admin only)
        var marketing = Project.builder()
                .id("p2")
                .name("Marketing")
                .description("Marketing root")
                .createdAt("2024-02-01")
                .allowedReadRoles(Set.of("Admin"))
                .allowedWriteRoles(Set.of("Admin"))
                .children(new ArrayList<>())
                .build();

        // ---------- Seed items ----------
        // Mixed items on Retail
        retail.getItems().addAll(List.of(
                new NoteItem("Kickoff next week."),
                new TaskItem("Plan Q1 promos", "u-1", "u-1", 1, 1694673114L, Priority.HIGH),
                new AttachmentItem("https://example.com/specs.pdf")
        ));

        // Attachments + note on Stock
        stock.getItems().addAll(List.of(
                new AttachmentItem("https://cdn.example.com/stock.csv"),
                new AttachmentItem("https://cdn.example.com/safety.pdf"),
                new NoteItem("Check reorder thresholds.")
        ));

        // Tasks on Marketing
        marketing.getItems().addAll(List.of(
                new TaskItem("Prepare campaign brief", "u-1", "u-1", 1, 1694673114L, Priority.MEDIUM),
                new TaskItem("Design assets", "u-2", "u-1", 2, 1694673114L, Priority.LOW)
        ));
        // ---------- End seed items ----------

        this.roots = List.of(retail, marketing);
    }

    public List<Project> getRoots() {
        return roots;
    }
}
