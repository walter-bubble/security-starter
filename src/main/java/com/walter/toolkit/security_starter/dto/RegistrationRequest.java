package com.walter.toolkit.security_starter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message ="Full Name  required")
        @Size(min=7,message = "name to short")
        String fullName,
        @NotBlank(message = "Email is required!")
        @Email(message = "please provide a valid emailAddress")
        @Size(max = 254, message = "emailAddress is to long")
        String email,

        @NotBlank(message = "password required!")
        @Size(min=12,max = 128)
        String password
) {
}
