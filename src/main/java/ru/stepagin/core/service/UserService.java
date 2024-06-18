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
        UserEntity userEntity = new UserEntity(registrationDto.getLogin(), registrationDto.getPassword());
        userEntity.setEmail(registrationDto.getEmail());
        return createUser(userEntity);
    }

    public UserDto createUser(UserEntity userEntity) {
        return UserMapper.toDto(userRepository.save(userEntity));
    }

    public void blockUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (userEntity.isBlocked()) {
            throw new NoChangesException("User already blocked");
        }
        if (userEntity.getRoles().contains(Role.MODERATOR)) {
            throw new IllegalActionException("Can not block a moderator");
        }
        userRepository.blockById(id);
    }

    public void unblockUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (!userEntity.isBlocked()) {
            throw new NoChangesException("User already unblocked");
        }
        userRepository.unblockById(id);
    }
}
