package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto newUser(UserDto userDto) {
        try {
            userDto = userMapper.toDto(userRepository.save(userMapper.toEntity(userDto)));
        } catch (Exception e) {
            throw new ConflictException("User with mail " + userDto.getEmail() + " already registered");
        }
        userDto.setId(userDto.getId());

        log.info("new user - id:'{}' email:'{}'", userDto.getId(), userDto.getEmail());

        return userDto;
    }

    @Override
    public UserDto updateUser(UserUpdateDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));

        userDto.setId(userId);

        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }

        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }

        UserDto newUserCreateDto = userMapper.toDto(
                userRepository.save(
                        userMapper.toEntity(userDto)
                )
        );

        log.info("update user - id:'{}' email:'{}'", userDto.getId(), userDto.getEmail());

        return newUserCreateDto;
    }

    @Override
    public UserDto getByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullObjectException("User with id: " + userId + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUserAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(toList());

    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}