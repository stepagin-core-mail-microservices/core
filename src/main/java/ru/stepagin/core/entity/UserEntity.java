package ru.stepagin.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.stepagin.core.config.SecurityConfiguration;
import ru.stepagin.core.security.Role;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person")
@Getter
@Setter
@RequiredArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "login", nullable = false, unique = true)
    private String login;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(unique = true)
    private String email;
    private boolean blocked;
    private Set<Role> roles = new HashSet<>();

    public UserEntity(String login, String rawPassword) {
        this.login = login;
        this.password = SecurityConfiguration.passwordEncoder().encode(rawPassword);
        this.roles.add(Role.USER);
        this.setBlocked(false);
    }
}
