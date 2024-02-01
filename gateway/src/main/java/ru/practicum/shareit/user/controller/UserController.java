package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.UserCreateValidation;
import ru.practicum.shareit.user.validation.UserUpdateValidation;

import javax.validation.constraints.Min;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting list of all users");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(UserCreateValidation.class) UserDto userDto) {
        log.info("Creating new user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") @Min(1) int id,
                                             @RequestBody @Validated(UserUpdateValidation.class) UserDto userDto) {
        log.info("Updating user with id:{}, new user data: {}", id, userDto);
        return userClient.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") @Min(1) int id) {
        log.info("Getting user by id={}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") @Min(1) int id) {
        log.info("Deleting user with id: {}", id);
        return userClient.deleteUser(id);
    }
}
