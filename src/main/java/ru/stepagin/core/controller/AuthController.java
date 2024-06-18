package ru.stepagin.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stepagin.core.dto.RegistrationDto;
import ru.stepagin.core.dto.UserDto;
import ru.stepagin.core.service.UserService;

@RestController
@RequestMapping("${api.endpoints.base-url}/auth")
@AllArgsConstructor
public class AuthController {
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<UserDto> registration(@Validated @RequestBody RegistrationDto registrationDto) {
        return ResponseEntity.ok(userService.registerUser(registrationDto));
    }
}
