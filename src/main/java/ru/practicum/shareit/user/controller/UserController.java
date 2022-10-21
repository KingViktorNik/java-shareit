package ru.practicum.shareit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;

    public UserController(UserServiceImpl userServiceImpl, UserMapper userMapper) {
        this.userServiceImpl = userServiceImpl;
        this.userMapper = userMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> newUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userServiceImpl.newUser(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateUser(@PathVariable @NotNull Long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        User user = userMapper.toEntity(userDto);
        user = userServiceImpl.updateUser(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getByUserId(@PathVariable @NotNull Long userId) {
        User user = userServiceImpl.getByUserId(userId);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping
    public List<UserDto> getUserAll() {
        List<User> users = userServiceImpl.getUserAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull Long userId) {
        userServiceImpl.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
