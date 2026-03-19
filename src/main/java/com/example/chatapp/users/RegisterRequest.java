package com.example.chatapp.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(description = "Только латиница, цифры, _ и -. Начинается с буквы.", example = "john_doe")
        @NotBlank
        @Size(min = 3, max = 20)
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "Никнейм может содержать только латиницу, цифры, _ и -")
        String username,

        @Schema(description = "Валидный email-адрес.", example = "john@example.com")
        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @Schema(description = "Только латиница, цифры, _ и -. Начинается с буквы.", example = "john_doe")
        @NotBlank
        @Size(min = 3, max = 20)
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]*$", message = "Пароль может содержать только латиницу, цифры, _ и -")
        String password
) {
}
