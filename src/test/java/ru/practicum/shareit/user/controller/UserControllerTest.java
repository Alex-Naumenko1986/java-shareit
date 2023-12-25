package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder().name("User 1").email("user1@yandex.ru").build();
    }

    @SneakyThrows
    @Test
    void getAllUsers_shouldReturnAllUsers() {
        UserDto userDto1 = userDto = UserDto.builder().id(2).name("User 1").email("user1@yandex.ru").build();
        userDto.setId(1);

        List<UserDto> users = List.of(userDto, userDto1);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(users)));

        verify(userService, Mockito.times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }


    @SneakyThrows
    @Test
    void createUser_shouldCreateUser() {
        UserDto createdUserDto = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        when(userService.createUser(userDto)).thenReturn(createdUserDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(createdUserDto)));

        verify(userService, Mockito.times(1)).createUser(userDto);
        verifyNoMoreInteractions(userService);
    }

    @SneakyThrows
    @Test
    void createUser_shouldThrowExceptionWhenCreatingUserWithInvalidName() {
        userDto.setName("");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUser_shouldThrowExceptionWhenCreatingUserWithInvalidEmail() {
        userDto.setEmail("123");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateUser_shouldUpdateUser() {
        UserDto updatedUserDto = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        when(userService.updateUser(updatedUserDto)).thenReturn(updatedUserDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedUserDto)));

        verify(userService, Mockito.times(1)).updateUser(updatedUserDto);
        verifyNoMoreInteractions(userService);
    }

    @SneakyThrows
    @Test
    void getUserById_shouldReturnUser() {
        userDto.setId(1);
        when(userService.getUserById(1)).thenReturn(userDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, Mockito.times(1)).getUserById(1);
        verifyNoMoreInteractions(userService);
    }

    @SneakyThrows
    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {
        when(userService.getUserById(1)).thenThrow(new UserNotFoundException("User with id 1 was not found"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(userService, Mockito.times(1)).getUserById(1);
        verifyNoMoreInteractions(userService);
    }

    @SneakyThrows
    @Test
    void deleteUser_shouldDeleteUser() {
        userDto.setId(1);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, Mockito.times(1)).deleteUser(1);
        verifyNoMoreInteractions(userService);
    }


}
