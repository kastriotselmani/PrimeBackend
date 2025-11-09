package com.example.backend;

import com.example.backend.repo.ProjectStore;
import com.example.backend.service.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    private final ProjectStore store = new ProjectStore();           // in-memory seed
    private final ProjectService service = new ProjectService(store); // pure unit test

    @Test
    void adminSeesAllProjects() {
        var tree = service.getVisibleTree(Set.of("Admin", "Analyst"));
        // from store: Retail -> Stock, Marketing
        assertEquals(2, tree.size(), "Admin should see both Retail and Marketing roots");
        var rootNames = tree.stream().map(p -> p.getName()).toList();
        assertTrue(rootNames.containsAll(List.of("Retail", "Marketing")));
    }

    @Test
    void analystCannotSeeMarketing() {
        var tree = service.getVisibleTree(Set.of("Analyst"));
        assertEquals(1, tree.size(), "Analyst should only see Retail");
        assertEquals("Retail", tree.get(0).getName());
        // but Analyst should still see child 'Stock' under Retail due to 'All' read
        var retailChildren = tree.get(0).getChildren();
        assertEquals(1, retailChildren.size());
        assertEquals("Stock", retailChildren.get(0).getName());
    }

    @Test
    void basicUserSeesOnlyAllReadableNodes() {
        var tree = service.getVisibleTree(Set.of("Basic"));
        // root 'Retail' requires Admin/Analyst (write implies read) -> hidden
        // root 'Marketing' requires Admin -> hidden
        // but child 'Stock' is under Retail: since parent is hidden, subtree is pruned
        assertEquals(0, tree.size(), "Basic should see nothing at root level");
    }
}
