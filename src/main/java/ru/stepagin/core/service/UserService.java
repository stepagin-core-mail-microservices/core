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
    private final KafkaProducerService kafkaProducerService;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDto registerUser(RegistrationDto registrationDto) {
        if (!registrationDto.getLogin().matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Login must contain only alphanumeric characters and underscores");
        }
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
        return saveUser(userEntity);
    }

    public UserDto saveUser(UserEntity userEntity) {
        UserEntity user = userRepository.save(userEntity);
        kafkaProducerService.sendUser(user);
        return UserMapper.toDto(userEntity);
    }

    public void blockUser(String login) {
        UserEntity user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException(login);
        }
        if (user.isBlocked()) {
            throw new NoChangesException("User already blocked");
        }
        if (user.getRoles().contains(Role.MODERATOR)) {
            throw new IllegalActionException("Cannot block a moderator");
        }
        userRepository.blockById(login);
        user.setBlocked(true);
        kafkaProducerService.sendUser(user);
    }

    public void unblockUser(String login) {
        UserEntity user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException(login);
        }
        if (!user.isBlocked()) {
            throw new NoChangesException("User already unblocked");
        }
        userRepository.unblockById(login);
        user.setBlocked(false);
        kafkaProducerService.sendUser(user);
    }

    public void updateEmail(UserEntity user, String email) {
        if (userRepository.existsByEmailIgnoreCase(email))
            throw new UserAlreadyExistsException("Email " + email + " is taken");
        userRepository.updateEmailByLogin(email, user.getLogin());
        user.setEmail(email);
        kafkaProducerService.sendUser(user);
    }

    public UserDto getUserByLogin(String login) {
        return UserMapper.toDto(userRepository.findByLogin(login));
    }
}
