package com.example.backend.auth;

import lombok.*; import java.util.Set;

@Getter @AllArgsConstructor
public class LoginResponse {
    public String token; public User user;

    @Getter @AllArgsConstructor
    public static class User {
        public String id; public String name; public Set<String> roles;
    }
}
