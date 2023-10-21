package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        User user = userService.createUser(userMapper.toUser(userDto));
        UserDto createdUserDto = userMapper.toUserDto(user);
        log.info("Created new user: {}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") int id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        User user = userService.updateUser(userMapper.toUser(userDto));
        UserDto updatedUserDto = userMapper.toUserDto(user);
        log.info("Updated user: {}", updatedUserDto);
        return updatedUserDto;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        return userMapper.toUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
        log.info("Deleted user with id: {}", id);
    }

}
