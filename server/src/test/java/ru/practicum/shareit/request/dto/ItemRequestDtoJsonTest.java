package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import utils.ResourcePool;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.itemRequestDto;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void itemRequestDto_shouldDeserialize() {
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder().id(1).description("Item description")
                .requestorId(1).created(LocalDateTime.of(2024, 1, 5, 0, 0)).build();

        ItemRequestDto actualItemRequestDto = json.readObject(itemRequestDto);

        assertEquals(expectedItemRequestDto, actualItemRequestDto);
    }

    @SneakyThrows
    @Test
    void itemRequestDto_shouldSerialize() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1).description("Item description")
                .requestorId(1).created(LocalDateTime.of(2024, 1, 5, 0, 0)).build();

        JsonContent<ItemRequestDto> itemRequestDtoAsJson = json.write(itemRequestDto);

        assertThat(itemRequestDtoAsJson).isEqualToJson(ResourcePool.itemRequestDto);
    }
}
