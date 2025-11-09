// src/test/java/com/example/backend/web/ProjectControllerTest.java
package com.example.backend.web;

import com.example.backend.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectControllerTest {

    @Test
    void mapsSpringRolesToPlainRoles() {
        ProjectService svc = Mockito.mock(ProjectService.class);
        Mockito.when(svc.getVisibleTree(Mockito.anySet())).thenReturn(List.of());

        ProjectController ctrl = new ProjectController(svc);

        var auth = new TestingAuthenticationToken(
                "user","N/A",
                List.of(new SimpleGrantedAuthority("ROLE_Admin"), new SimpleGrantedAuthority("ROLE_Analyst"))
        );

        // ✅ pass mode=null to match controller signature (no query param)
        Map<String,Object> resp = ctrl.all(auth, null);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<String>> captor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(svc).getVisibleTree(captor.capture());

        assertThat(captor.getValue()).containsExactlyInAnyOrder("Admin","Analyst");
        assertThat(resp).containsKey("projects");
    }
}
