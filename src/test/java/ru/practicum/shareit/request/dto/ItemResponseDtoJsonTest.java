package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ShortItemDto;
import utils.ResourcePool;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.itemResponseDto;

@JsonTest
public class ItemResponseDtoJsonTest {
    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    @SneakyThrows
    void itemResponseDto_shouldDeserialize() {
        ShortItemDto item1 = ShortItemDto.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).build();
        ShortItemDto item2 = ShortItemDto.builder().id(2).name("Item 2").description("Item 2 description")
                .available(true).requestId(1).build();

        ItemResponseDto expectedItemResponseDto = ItemResponseDto.builder().id(1).description("Description")
                .created(LocalDateTime.of(2023, 12, 8, 0, 0))
                .items(List.of(item1, item2)).build();

        ItemResponseDto actualItemResponseDto = json.readObject(itemResponseDto);

        assertEquals(expectedItemResponseDto, actualItemResponseDto);
    }

    @SneakyThrows
    @Test
    void itemResponseDto_shouldSerialize() {
        ShortItemDto item1 = ShortItemDto.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).build();
        ShortItemDto item2 = ShortItemDto.builder().id(2).name("Item 2").description("Item 2 description")
                .available(true).requestId(1).build();

        ItemResponseDto itemResponseDto = ItemResponseDto.builder().id(1).description("Description")
                .created(LocalDateTime.of(2023, 12, 8, 0, 0))
                .items(List.of(item1, item2)).build();

        JsonContent<ItemResponseDto> itemResponseDtoAsJson = json.write(itemResponseDto);

        assertThat(itemResponseDtoAsJson).isEqualToJson(ResourcePool.itemResponseDto);
    }
}
