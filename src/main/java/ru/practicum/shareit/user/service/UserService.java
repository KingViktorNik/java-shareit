package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User newUser(User user);

    User updateUser(User updateUser);

    User getByUserId(Long userId);

    List<User> getUserAll();
}