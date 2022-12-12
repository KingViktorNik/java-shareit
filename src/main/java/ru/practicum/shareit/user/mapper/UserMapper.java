package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public static User toEntity(UserDto userCreateDto) {
        User user = new User();
        user.setId(userCreateDto.getId());
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());
        return user;
    }

    public static User toEntity(UserUpdateDto userCreateDto) {
        User user = new User();
        user.setId(userCreateDto.getId());
        user.setEmail(userCreateDto.getEmail());
        user.setName(userCreateDto.getName());
        return user;
    }

    public static UserDto toDto(User user) {
        UserDto userCreateDto = new UserDto();
        userCreateDto.setId(user.getId());
        userCreateDto.setEmail(user.getEmail());
        userCreateDto.setName(user.getName());
        return userCreateDto;
    }
}