package com.example.backend.auth;

import com.example.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Authenticate and get a JWT (public)")
    @PostMapping("/authenticate")
    public Map<String, Object> authenticate(@Valid @RequestBody LoginRequest req) {
        var res  = authService.authenticate(req.emailOrUsername(), req.password());
        var user = res.user();

        return Map.of(
                "token", res.token(),
                "user", Map.of(
                        "id",   user.getId(),
                        "name", user.getUsernameOrEmail(),
                        "roles", res.roles()
                )
        );
    }

    @GetMapping("/whoami")
    public Map<String, Object> whoami(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authentication");
        }
        return Map.of(
                "sub", auth.getName(),
                "authorities", auth.getAuthorities()
        );
    }
}
