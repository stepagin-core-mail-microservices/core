package ru.stepagin.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.stepagin.core.entity.UserEntity;
import ru.stepagin.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;

    public boolean isModerator(Authentication authentication) {
        UserEntity user = getUserEntity(authentication);
        return !user.isBlocked() && user.getRoles().contains(Role.MODERATOR);
    }

    public UserEntity getUserEntity(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return userRepository.findByLogin(user.getUsername());
    }

    public boolean isBlocked(Authentication authentication) {
        UserEntity user = getUserEntity(authentication);
        return user.isBlocked();
    }


}
