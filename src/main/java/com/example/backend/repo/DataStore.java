package com.example.backend.repo;

import com.example.backend.domain.*;
import org.springframework.stereotype.Component;
import java.util.*;
import com.example.backend.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataStore {
    private final Map<String, UserAccount> users = new HashMap<>();
    public DataStore(PasswordEncoder encoder) {
        users.put("pm@demo.io", UserAccount.builder()
                .id("u-1").usernameOrEmail("pm@demo.io")
                .password(encoder.encode("pass"))
                .roles(Set.of(Role.Admin, Role.Analyst))
                .build());

        users.put("analyst@demo.io", UserAccount.builder()
                .id("u-2").usernameOrEmail("analyst@demo.io")
                .password(encoder.encode("pass"))
                .roles(Set.of(Role.Analyst))
                .build());

        users.put("basic@example.com", UserAccount.builder()
                .id("u-3").usernameOrEmail("basic@example.com")
                .password(encoder.encode("basic123"))   // store BCrypt hash
                .roles(Set.of(Role.Basic))
                .build());
    }

    public Optional<UserAccount> findUser(String u){ return Optional.ofNullable(users.get(u)); }
}
