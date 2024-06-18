package ru.stepagin.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    MODERATOR("MODERATOR"),
    USER("USER");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }

}
