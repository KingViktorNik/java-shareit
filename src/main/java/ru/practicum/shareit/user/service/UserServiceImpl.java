package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User newUser(User user) {
        user.setId(userRepository.create(user));

        if (user.getId() == null) {
            throw new ConflictException("User with mail " + user.getEmail() + " already registered");
        }

        log.info("new user - id:'{}' email:'{}'", user.getId(), user.getEmail());
        return user;
    }

    public User updateUser(User updateUser) {
        User user = userRepository.getById(updateUser.getId());

        if (user == null) {
            throw new NullObjectException("User with id: " + updateUser.getId() + " not found");
        }

        if (updateUser.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            if (!isAddress(updateUser.getEmail())) {
                throw new ValidationException("The postal address '" + updateUser.getEmail() + "' is not correct");
            }
            if (userRepository.getByEmail(updateUser.getEmail()) != null) {
                throw new ConflictException("User with mail " + user.getEmail() + " already registered");
            }
            user.setEmail(updateUser.getEmail());
        }

        if (updateUser.getName() != null && !updateUser.getName().equals(user.getName())) {
            if (updateUser.getName().isEmpty() || updateUser.getName().isBlank()) {
                throw new ValidationException("The postal address '" + updateUser.getName() + "' is not correct");
            }
            user.setName(updateUser.getName().trim());
        }

        user = userRepository.update(user);
        log.info("update user - id:'{}' email:'{}'", user.getId(), user.getEmail());
        return user;
    }

    public User getByUserId(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new NullObjectException("User with id: " + userId + " not found");
        }
        return user;
    }

    public List<User> getUserAll() {
        return userRepository.getAll();
    }

    public void deleteUser(Long userId) {
        if (userRepository.getById(userId) != null) {
            userRepository.delete(userId);
            return;
        }
        throw new NullObjectException("user with id = " + userId + " not found for delete");
    }

    private boolean isAddress(String email) {
        Pattern pattern = Pattern.compile("(?:[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\" +
                ".[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
                "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)" +
                "+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\" +
                ".){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]" +
                ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}