package ru.practicum.shareit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public ResponseEntity<UserDto> newUser(@RequestBody UserDto userCreateDto) {
        return ResponseEntity.ok(userServiceImpl.newUser(userCreateDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userServiceImpl.updateUser(userDto, userId));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userServiceImpl.getByUserId(userId));
    }

    @GetMapping
    public List<UserDto> getUserAll() {
        return userServiceImpl.getUserAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userServiceImpl.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
