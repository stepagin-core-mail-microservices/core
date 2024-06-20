package ru.stepagin.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmailDto {
    @Email
    @Size(min = 6, max = 255, message = "должен быть от 6 до 255 символов")
    private String email;
}
