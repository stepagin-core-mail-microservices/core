package ru.stepagin.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.core.entity.UserEntity;
import ru.stepagin.core.service.UserService;

import java.util.List;

@RestController
@RequestMapping("${api.endpoints.base-url}/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<List<UserEntity>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<Object> block(@PathVariable("id") Long id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("@securityService.isModerator(authentication)")
    public ResponseEntity<Object> unblock(@PathVariable("id") Long id) {
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }


}
