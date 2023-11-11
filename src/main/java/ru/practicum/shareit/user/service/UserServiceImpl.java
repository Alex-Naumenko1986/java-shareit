package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.db.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public List<UserDto> getAllUsers() {
        List<UserEntity> userEntities = userStorage.findAll();
        return userEntities.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity = userStorage.save(userEntity);
        return userMapper.toDto(userEntity);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntityUpdated = userMapper.toEntity(userDto);
        UserEntity userEntity = userStorage.findById(userEntityUpdated.getId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userEntityUpdated.getId())));
        updateUserFields(userEntity, userEntityUpdated);
        userEntity = userStorage.save(userEntity);
        return userMapper.toDto(userEntity);
    }

    @Override
    @Transactional
    public UserDto getUserById(int id) {
        UserEntity userEntity = userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found", id)));
        return userMapper.toDto(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        userStorage.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found", id)));
        userStorage.deleteById(id);
    }

    private void updateUserFields(UserEntity userEntityFromStorage, UserEntity userEntity) {

        if (userEntity.getName() != null && !userEntity.getName().isBlank()) {
            userEntityFromStorage.setName(userEntity.getName());
        }

        if (userEntity.getEmail() != null && !userEntity.getEmail().isBlank()) {
            userEntityFromStorage.setEmail(userEntity.getEmail());
        }
    }
}
