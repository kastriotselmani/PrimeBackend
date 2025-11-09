// src/test/java/com/example/backend/service/AuthServiceTest.java
package com.example.backend.service;

import com.example.backend.auth.JwtService;
import com.example.backend.domain.Role;
import com.example.backend.domain.UserAccount;
import com.example.backend.repo.DataStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

class AuthServiceTest {

    private DataStore store;
    private JwtService jwt;
    private PasswordEncoder encoder;
    private AuthService svc;

    @BeforeEach
    void setup() {
        store = Mockito.mock(DataStore.class);
        jwt = Mockito.mock(JwtService.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        svc = new AuthService(store, jwt, encoder);
    }

    @Test
    void authenticate_success_returnsTokenAndRoles() {
        // given
        String username = "pm@demo.io";
        String rawPassword = "pass";
        String hash = "$2a$10$whatever"; // not used by matches we stub it.
        var user = UserAccount.builder()
                .id("u-1")
                .usernameOrEmail(username)
                .password(hash)
                .roles(Set.of(Role.Admin, Role.Analyst))
                .build();

        Mockito.when(store.findUser(eq(username))).thenReturn(Optional.of(user));
        // called twice in your AuthService: once in filter, once again after orElseThrow
        Mockito.when(encoder.matches(eq(rawPassword), eq(hash))).thenReturn(true);
        Mockito.when(jwt.generate(eq(username), anyMap())).thenReturn("jwt-token");

        // when
        AuthService.AuthResult result = svc.authenticate(username, rawPassword);

        // then
        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.user()).isEqualTo(user);
        assertThat(result.roles()).containsExactlyInAnyOrder("Admin", "Analyst");

        // and verify interactions
        Mockito.verify(store).findUser(username);
        Mockito.verify(encoder, Mockito.times(2)).matches(rawPassword, hash);
        Mockito.verify(jwt).generate(eq(username), anyMap());
    }

    @Test
    void authenticate_wrongPassword_throws401() {
        String username = "pm@demo.io";
        String rawPassword = "wrong";
        String hash = "$2a$10$whatever";
        var user = UserAccount.builder()
                .id("u-1")
                .usernameOrEmail(username)
                .password(hash)
                .roles(Set.of(Role.Admin))
                .build();

        Mockito.when(store.findUser(eq(username))).thenReturn(Optional.of(user));
        Mockito.when(encoder.matches(eq(rawPassword), eq(hash))).thenReturn(false);

        var ex = assertThrows(ResponseStatusException.class,
                () -> svc.authenticate(username, rawPassword));

        assertThat(ex.getStatusCode().value()).isEqualTo(401);
        assertThat(ex.getReason()).containsIgnoringCase("bad credentials");

        Mockito.verify(jwt, Mockito.never()).generate(anyString(), anyMap());
    }

    @Test
    void authenticate_userNotFound_throws401() {
        Mockito.when(store.findUser(eq("nope@demo.io"))).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class,
                () -> svc.authenticate("nope@demo.io", "pass"));

        assertThat(ex.getStatusCode().value()).isEqualTo(401);
        assertThat(ex.getReason()).containsIgnoringCase("bad credentials");
        Mockito.verify(encoder, Mockito.never()).matches(anyString(), anyString());
        Mockito.verify(jwt, Mockito.never()).generate(anyString(), anyMap());
    }
}
