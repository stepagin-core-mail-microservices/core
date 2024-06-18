package ru.stepagin.core.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {
    @NotNull(message = "не может быть null")
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String login;
    @NotNull(message = "не может быть null")
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String password;
    @Nullable
    @Email
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String email;
}
