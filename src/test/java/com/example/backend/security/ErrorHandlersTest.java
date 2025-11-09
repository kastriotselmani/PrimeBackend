package com.example.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorHandlersTest {

    @Autowired MockMvc mvc;

    @Test void unauthorized_isUnifiedJson() throws Exception {
        mvc.perform(get("/api/projects/all"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.path").value("/api/projects/all"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test void forbidden_isUnifiedJson() throws Exception {
        // Use a non-admin token if you have a generator; here assume placeholder:
        mvc.perform(get("/api/admin/ping").header(HttpHeaders.AUTHORIZATION, "Bearer <basic-or-analyst-token>"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("access")));
    }
}
