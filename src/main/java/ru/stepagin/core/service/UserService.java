package ru.stepagin.core.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.stepagin.core.dto.RegistrationDto;
import ru.stepagin.core.dto.UserDto;
import ru.stepagin.core.entity.UserEntity;
import ru.stepagin.core.exception.IllegalActionException;
import ru.stepagin.core.exception.NoChangesException;
import ru.stepagin.core.exception.UserAlreadyExistsException;
import ru.stepagin.core.exception.UserNotFoundException;
import ru.stepagin.core.mapper.UserMapper;
import ru.stepagin.core.repository.UserRepository;
import ru.stepagin.core.security.Role;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDto registerUser(RegistrationDto registrationDto) {
        if (userRepository.existsByLogin(registrationDto.getLogin())) {
            throw new UserAlreadyExistsException("User with login " + registrationDto.getLogin() + " already exists");
        }
        String email = registrationDto.getEmail();
        if (email != null && !email.isEmpty()) {
            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new UserAlreadyExistsException("Email " + email + " is taken");
            }
        }
        UserEntity userEntity = new UserEntity(registrationDto.getLogin(), registrationDto.getPassword());
        userEntity.setEmail(registrationDto.getEmail());
        return createUser(userEntity);
    }

    public UserDto createUser(UserEntity userEntity) {
        return UserMapper.toDto(userRepository.save(userEntity));
    }

    public void blockUser(String login) {
        UserEntity userEntity = userRepository.findByLogin(login);
        if (userEntity == null) {
            throw new UserNotFoundException(login);
        }
        if (userEntity.isBlocked()) {
            throw new NoChangesException("User already blocked");
        }
        if (userEntity.getRoles().contains(Role.MODERATOR)) {
            throw new IllegalActionException("Cannot block a moderator");
        }
        userRepository.blockById(login);
    }

    public void unblockUser(String login) {
        UserEntity userEntity = userRepository.findByLogin(login);
        if (userEntity == null) {
            throw new UserNotFoundException(login);
        }
        if (!userEntity.isBlocked()) {
            throw new NoChangesException("User already unblocked");
        }
        userRepository.unblockById(login);
    }

    public void updateEmail(UserEntity user, String email) {
        if (userRepository.existsByEmailIgnoreCase(email))
            throw new UserAlreadyExistsException("Email " + email + " is taken");
        userRepository.updateEmailByLogin(email, user.getLogin());
    }

    public UserDto getUserByLogin(String login) {
        return UserMapper.toDto(userRepository.findByLogin(login));
    }
}
