package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto newUser(UserDto userDto);

    UserDto updateUser(UserDto updateUserDto);

    UserDto getByUserId(Long userId);

    List<UserDto> getUserAll();

    void deleteUser(Long userId);
}