package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto newUser(UserDto userDto);

    UserDto updateUser(UserUpdateDto userDto, Long userId);

    UserDto getByUserId(Long userId);

    List<UserDto> getUserAll();

    void deleteUser(Long userId);
}