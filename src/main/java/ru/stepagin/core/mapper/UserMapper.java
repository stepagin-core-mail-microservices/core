package ru.stepagin.core.mapper;

import ru.stepagin.core.dto.UserDto;
import ru.stepagin.core.entity.UserEntity;

public abstract class UserMapper {
    public static UserDto toDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
