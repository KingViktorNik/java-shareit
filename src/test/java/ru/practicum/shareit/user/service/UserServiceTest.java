package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NullObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private UserDto userDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@email.com");
        userDto = new UserDto(null, "User", "user@email.com");
        userUpdateDto = new UserUpdateDto(null, "UserNew", "userNew@email.com");
    }

    @Test
    void newUserNullUserId() {
        // given
        when(userRepository.save(any(User.class)))
                .thenReturn(null);
        // when
        final ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.newUser(userDto)
        );

        // then
        Assertions.assertEquals("User with mail User already registered", exception.getMessage());
    }

    @Test
    void updateUserNotFount() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> userService.updateUser(userUpdateDto, 555L)
        );

        // then
        Assertions.assertEquals("User with id: 555 not found", exception.getMessage());
    }
}