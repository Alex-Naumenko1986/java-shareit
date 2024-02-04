package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import utils.ResourcePool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.userDto;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void userDto_shouldDeserialize() {
        UserDto expectedUserDto = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();

        UserDto actualUserDto = json.readObject(userDto);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @SneakyThrows
    @Test
    void userDto_shouldSerialize() {
        UserDto userDto = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();

        JsonContent<UserDto> userDtoAsJson = json.write(userDto);

        assertThat(userDtoAsJson).isEqualToJson(ResourcePool.userDto);
    }
}
