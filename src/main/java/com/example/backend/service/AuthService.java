package com.example.backend.service;

import com.example.backend.auth.JwtService;
import com.example.backend.domain.UserAccount;
import com.example.backend.repo.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final DataStore store;
    private final JwtService jwt;
    private final PasswordEncoder encoder;

    public AuthService(DataStore store, JwtService jwt, PasswordEncoder encoder) {
        this.store = store;
        this.jwt = jwt;
        this.encoder = encoder;
    }

    // Simple result to return to controller
    public record AuthResult(String token, UserAccount user, Set<String> roles) {}

    public AuthResult authenticate(String userOrEmail, String password) {
        var u = store.findUser(userOrEmail)
                .filter(x -> encoder.matches(password, x.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        //  raw password vs hash
        if (!encoder.matches(password, u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }

        Set<String> roles = u.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        String token = jwt.generate(
                u.getUsernameOrEmail(),
                Map.of("uid", u.getId(), "roles", roles)
        );

        return new AuthResult(token, u, roles);
    }
}
