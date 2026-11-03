package com.akabli.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le nom est obligatoire") String fullName,
        @NotBlank @Email(message = "Email invalide") String email,
        @NotBlank @Size(min = 6, message = "6 caractères minimum") String password
) {
}
