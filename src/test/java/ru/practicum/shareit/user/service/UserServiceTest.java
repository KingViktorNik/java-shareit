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


import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
    void newUserHappy() {
        // given
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        // when
        userService.newUser(userDto);

        //then
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
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

    @Test
    void updateUserHappy() {
        // given
        User userNew = new User(1L, "UserNew", "userNew@email.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(userNew);

        // when
        UserDto userDtoTest = userService.updateUser(userUpdateDto, 1L);

        //then
        assertThat(userDtoTest.getId(), equalTo(user.getId()));
        assertThat(userDtoTest.getName(), equalTo(userNew.getName()));
        assertThat(userDtoTest.getEmail(), equalTo(userNew.getEmail()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getByUserIdNotFound() {
        // when
        final NullObjectException exception = assertThrows(
                NullObjectException.class,
                () -> userService.getByUserId(555L)
        );

        // then
        Assertions.assertEquals("User with id: 555 not found", exception.getMessage());
    }

    @Test
    void getByUserIdHappy() {
        // given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        // when
        UserDto userDtoTest = userService.getByUserId(1L);

        //then
        assertThat(userDtoTest.getId(), notNullValue());
        assertThat(userDtoTest.getEmail(), equalTo(userDtoTest.getEmail()));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserAll() {
        // given
        List<User> users = List.of(
                new User(1L, "user1", "user1@email.com"),
                new User(2L, "user2", "user2@email.com"),
                new User(3L, "user3", "user3@email.com")
        );
        when(userRepository.findAll())
                .thenReturn(users);

        // when
        List<UserDto> userDtoTest = userService.getUserAll();

        //then
        assertThat(userDtoTest, hasSize(3));
        for (User sourceUser : users) {
            assertThat(userDtoTest, hasItem(allOf(
                    hasProperty("id", equalTo(sourceUser.getId())),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}