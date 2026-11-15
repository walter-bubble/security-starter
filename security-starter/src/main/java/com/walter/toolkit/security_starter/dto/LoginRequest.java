package com.walter.toolkit.security_starter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email required")
        @Email(message = "provide a valid emailAddress")
        String emailAddress,
        @NotBlank(message = "password required")
        String password
) {
}
