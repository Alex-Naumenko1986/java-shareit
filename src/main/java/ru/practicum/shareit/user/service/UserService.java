package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    void deleteUser(int id);
}
