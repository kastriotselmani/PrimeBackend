package com.example.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// End-to-end with full context
@SpringBootTest
@AutoConfigureMockMvc
class RbacIntegrationTest {

    // Replace these with real tokens you can generate via a JwtService test util if you have one
    private static final String ADMIN_JWT = "Bearer <admin-token>";
    private static final String ANALYST_JWT = "Bearer <analyst-token>";
    private static final String BASIC_JWT = "Bearer <basic-token>";

    @Autowired MockMvc mvc;

    @Test void admin_ping_requires_auth() throws Exception {
        mvc.perform(get("/api/admin/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test void admin_ping_admin_ok() throws Exception {
        mvc.perform(get("/api/admin/ping").header(HttpHeaders.AUTHORIZATION, ADMIN_JWT))
                .andExpect(status().isOk());
    }

    @Test void admin_ping_analyst_forbidden() throws Exception {
        mvc.perform(get("/api/admin/ping").header(HttpHeaders.AUTHORIZATION, ANALYST_JWT))
                .andExpect(status().isForbidden());
    }

    @Test void admin_ping_basic_forbidden() throws Exception {
        mvc.perform(get("/api/admin/ping").header(HttpHeaders.AUTHORIZATION, BASIC_JWT))
                .andExpect(status().isForbidden());
    }
}
