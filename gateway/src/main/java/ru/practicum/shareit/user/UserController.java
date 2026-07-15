package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {

        log.info("Get Users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get user with userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto userDto) {
        log.info("Post user with userDto={}", userDto);
        return userClient.saveUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Delete user with userId={}", userId);
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUserById(@PathVariable long userId,
                                                 @RequestBody UserDto userDto) {
        log.info("Update user with userId={}, userDto={}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }
    }
