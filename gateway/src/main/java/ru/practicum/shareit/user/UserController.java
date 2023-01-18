package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)

public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newUser(@Valid @RequestBody UserDto userCreateDto) {
        ResponseEntity<Object> result = ResponseEntity.ok(userClient.newUser(userCreateDto));
        Map<String, Object> body = (Map) result.getBody();
        log.info("[POST:{}] add user[id:{} email:{}]",
                result.getStatusCode(),
                body.get("id"),
                body.get("email")
        );
        return result;
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@Valid @PathVariable Long userId,
                                             @Valid @RequestBody UserUpdateDto userDto) {
        userDto.setId(userId);
        ResponseEntity<Object> result = ResponseEntity.ok(userClient.updateUser(userId, userDto));
        log.info("[PATCH:{}] update user[id:{} email:{}]",
                result.getStatusCode(),
                userDto.getId(),
                userDto.getEmail()
        );
        return result;
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getByUserId(@Valid @PathVariable @NotNull Long userId) {
        ResponseEntity<Object> result = ResponseEntity.ok(userClient.getByUserId(userId));
        Map<String, Object> body = (Map) result.getBody();
        log.info("[GET:{}] user[id:{} email:{}]",
                result.getStatusCode(),
                body.get("id"),
                body.get("email")
        );
        return result;
    }

    @GetMapping
    public Object getUserAll() {
        ResponseEntity<Object> result = ResponseEntity.ok(userClient.getUserAll());
        log.info("[GET:{}] all users[size:{}]",
                result.getStatusCode(),
                ((List) result.getBody()).size());
        return result;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable @NotNull Long userId) {
        ResponseEntity<Object> result = ResponseEntity.ok(userClient.deleteUser(userId));
        log.info("[DELETE:{}] user[id:{}]", result.getStatusCode(), userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
