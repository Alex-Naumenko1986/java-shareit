package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import utils.ResourcePool;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.itemDto;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void itemDto_shouldDeserialize() {
        CommentDto comment1 = CommentDto.builder().id(1).text("Comment 1").authorName("User 2")
                .created(LocalDateTime.of(2023, 12, 1, 10, 30)).build();
        CommentDto comment2 = CommentDto.builder().id(2).text("Comment 2").authorName("User 2")
                .created(LocalDateTime.of(2023, 11, 30, 10, 30)).build();
        BookingInfoDto lastBooking = BookingInfoDto.builder().id(1).bookerId(2)
                .start(LocalDateTime.of(2023, 11, 10, 8, 14))
                .end(LocalDateTime.of(2024, 1, 10, 8, 14)).build();
        BookingInfoDto nextBooking = BookingInfoDto.builder().id(2).bookerId(2)
                .start(LocalDateTime.of(2024, 1, 10, 8, 14))
                .end(LocalDateTime.of(2024, 2, 10, 8, 14)).build();

        ItemDto expectedItemDto = ItemDto.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).comments(List.of(comment1, comment2)).lastBooking(lastBooking)
                .nextBooking(nextBooking).build();

        ItemDto actualItemDto = json.readObject(itemDto);
        assertEquals(expectedItemDto, actualItemDto);
    }

    @SneakyThrows
    @Test
    void itemDto_shouldSerialize() {
        CommentDto comment1 = CommentDto.builder().id(1).text("Comment 1").authorName("User 2")
                .created(LocalDateTime.of(2023, 12, 1, 10, 30)).build();
        CommentDto comment2 = CommentDto.builder().id(2).text("Comment 2").authorName("User 2")
                .created(LocalDateTime.of(2023, 11, 30, 10, 30)).build();
        BookingInfoDto lastBooking = BookingInfoDto.builder().id(1).bookerId(2)
                .start(LocalDateTime.of(2023, 11, 10, 8, 14))
                .end(LocalDateTime.of(2024, 1, 10, 8, 14)).build();
        BookingInfoDto nextBooking = BookingInfoDto.builder().id(2).bookerId(2)
                .start(LocalDateTime.of(2024, 1, 10, 8, 14))
                .end(LocalDateTime.of(2024, 2, 10, 8, 14)).build();

        ItemDto itemDto = ItemDto.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).comments(List.of(comment1, comment2)).lastBooking(lastBooking)
                .nextBooking(nextBooking).build();

        JsonContent<ItemDto> itemDtoAsJson = json.write(itemDto);

        assertThat(itemDtoAsJson).isEqualToJson(ResourcePool.itemDto);
    }
}
