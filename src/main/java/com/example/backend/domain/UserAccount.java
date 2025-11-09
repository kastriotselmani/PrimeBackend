package com.example.backend.domain;

import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {
    private String id, usernameOrEmail, password;
    private Set<Role> roles;
}
