package com.example.chatapp.messages;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank(message = "Сообщение не может быть пустым")
        String content
) {
}
