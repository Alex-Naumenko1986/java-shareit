package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import utils.ResourcePool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.shortItemDto;

@JsonTest
public class ShortItemDtoJsonTest {
    @Autowired
    JacksonTester<ShortItemDto> json;

    @SneakyThrows
    @Test
    void shortItemDto_shouldDeserialize() {
        ShortItemDto expectedShortItemDto = ShortItemDto.builder().id(1).name("Item 1")
                .description("Item 1 description").requestId(1).available(true).build();

        ShortItemDto actualShortItemDto = json.readObject(shortItemDto);

        assertEquals(expectedShortItemDto, actualShortItemDto);
    }

    @SneakyThrows
    @Test
    void shortItemDto_shouldSerialize() {
        ShortItemDto shortItemDto = ShortItemDto.builder().id(1).name("Item 1")
                .description("Item 1 description").requestId(1).available(true).build();

        JsonContent<ShortItemDto> shortItemDtoAsJson = json.write(shortItemDto);

        assertThat(shortItemDtoAsJson).isEqualToJson(ResourcePool.shortItemDto);
    }
}
