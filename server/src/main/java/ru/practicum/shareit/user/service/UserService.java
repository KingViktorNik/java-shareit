package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto newUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto getByUserId(Long userId);

    List<UserDto> getUserAll();

    void deleteUser(Long userId);
}