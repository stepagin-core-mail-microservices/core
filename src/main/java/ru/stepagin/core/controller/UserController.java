package ru.stepagin.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.core.dto.UpdateEmailDto;
import ru.stepagin.core.dto.UserDto;
import ru.stepagin.core.entity.UserEntity;
import ru.stepagin.core.security.SecurityService;
import ru.stepagin.core.service.UserService;

import java.util.List;

@RestController
@RequestMapping("${api.endpoints.base-url}/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private final SecurityService securityService;

    @GetMapping
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<List<UserEntity>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{login}/block")
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<Object> block(@PathVariable("login") String login) {
        userService.blockUser(login);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{login}/unblock")
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<Object> unblock(@PathVariable("login") String login) {
        userService.unblockUser(login);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{login}")
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<UserDto> getUser(@PathVariable("login") String login) {
        return ResponseEntity.ok(userService.getUserByLogin(login));
    }

    @PutMapping("/email")
    @PreAuthorize("!@securityService.isBlocked(authentication)")
    public ResponseEntity<Object> updateEmail(@Validated @RequestBody UpdateEmailDto emailDto, Authentication auth) {
        UserEntity user = securityService.getUserEntity(auth);
        userService.updateEmail(user, emailDto.getEmail());
        return ResponseEntity.noContent().build();
    }


}
