package ru.practicum.shareit.user.storage.in_memory;

import ru.practicum.shareit.user.model.UserEntity;

import java.util.List;

public interface InMemoryUserStorage {
    List<UserEntity> getAllUsers();

    UserEntity createUser(UserEntity userEntity);

    UserEntity updateUser(UserEntity userEntity);

    UserEntity getUserById(int id);

    void deleteUser(int id);
}
