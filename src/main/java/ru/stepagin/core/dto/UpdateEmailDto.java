package ru.stepagin.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateEmailDto {
    @NotNull(message = "не может быть null")
    @Email
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String email;
}
