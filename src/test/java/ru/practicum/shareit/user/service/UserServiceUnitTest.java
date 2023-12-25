package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    UserStorage userStorage;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;

    UserEntity userEntity1;
    UserEntity userEntity2;
    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        userEntity1 = UserEntity.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        userEntity2 = UserEntity.builder().id(2).name("User 2").email("user2@yandex.ru").build();
        userDto1 = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        userDto2 = UserDto.builder().id(2).name("User 2").email("user2@yandex.ru").build();
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(userStorage.findAll()).thenReturn(List.of(userEntity1, userEntity2));
        when(userMapper.toDto(userEntity1)).thenReturn(userDto1);
        when(userMapper.toDto(userEntity2)).thenReturn(userDto2);

        List<UserDto> actualUserDtos = userService.getAllUsers();

        assertEquals(List.of(userDto1, userDto2), actualUserDtos);
        Mockito.verify(userStorage, Mockito.times(1)).findAll();
        Mockito.verify(userMapper, Mockito.times(2)).toDto(any());
    }

    @Test
    void createUser_shouldCreateUser() {
        when(userStorage.save(userEntity1)).thenReturn(userEntity1);
        when(userMapper.toDto(userEntity1)).thenReturn(userDto1);
        when(userMapper.toEntity(userDto1)).thenReturn(userEntity1);

        UserDto createdUser = userService.createUser(userDto1);

        Mockito.verify(userStorage, Mockito.times(1)).save(userEntity1);
        assertEquals(userDto1, createdUser);
        Mockito.verify(userMapper, Mockito.times(1)).toDto(any());
        Mockito.verify(userMapper, Mockito.times(1)).toEntity(any());
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto1)).thenReturn(userEntity1);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userDto1));
        assertEquals("User with id 1 was not found", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateUser() {
        UserEntity userEntityUpdated = UserEntity.builder().id(1).name("User 1 updated").email("updated@yandex.ru")
                .build();
        UserDto userDtoUpdated = UserDto.builder().id(1).name("User 1 updated").email("updated@yandex.ru")
                .build();
        when(userStorage.findById(1)).thenReturn(Optional.of(userEntity1));
        when(userStorage.save(any())).thenReturn(userEntityUpdated);
        when(userMapper.toEntity(userDto1)).thenReturn(userEntity1);
        when(userMapper.toDto(userEntityUpdated)).thenReturn(userDtoUpdated);

        UserDto actualUserDto = userService.updateUser(userDto1);
        assertEquals(userDtoUpdated, actualUserDto);
        Mockito.verify(userStorage, Mockito.times(1)).save(any());
        Mockito.verify(userMapper, Mockito.times(1)).toDto(any());
        Mockito.verify(userMapper, Mockito.times(1)).toEntity(any());
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1));
        assertEquals("User with id 1 was not found", exception.getMessage());
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userStorage.findById(1)).thenReturn(Optional.of(userEntity1));
        when(userMapper.toDto(userEntity1)).thenReturn(userDto1);

        UserDto actualUser = userService.getUserById(1);

        assertEquals(userDto1, actualUser);
        Mockito.verify(userStorage, Mockito.times(1)).findById(1);
        Mockito.verify(userMapper, Mockito.times(1)).toDto(userEntity1);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1));
        assertEquals("User with id 1 was not found", exception.getMessage());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userStorage.findById(1)).thenReturn(Optional.of(userEntity1));

        userService.deleteUser(1);

        Mockito.verify(userStorage, Mockito.times(1)).findById(1);
        Mockito.verify(userStorage, Mockito.times(1)).deleteById(1);

    }
}
