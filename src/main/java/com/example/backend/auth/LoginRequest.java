package com.example.backend.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "emailOrUsername is required")
        String emailOrUsername,

        @NotBlank(message = "password is required")
        String password
) {}