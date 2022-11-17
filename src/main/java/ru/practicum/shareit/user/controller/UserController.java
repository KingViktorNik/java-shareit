package ru.practicum.shareit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> newUser(@RequestBody @Valid UserDto userCreateDto) {
        return ResponseEntity.ok(userServiceImpl.newUser(userCreateDto));
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateDto userDto) {
        return ResponseEntity.ok(userServiceImpl.updateUser(userDto, userId));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getByUserId(@PathVariable @NotNull Long userId) {
        return ResponseEntity.ok(userServiceImpl.getByUserId(userId));
    }

    @GetMapping
    public List<UserDto> getUserAll() {
        return userServiceImpl.getUserAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull Long userId) {
        userServiceImpl.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
