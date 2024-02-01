package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import utils.ResourcePool;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.bookingDto;


@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void bookingDto_shouldDeserialize() {
        ItemDto item = ItemDto.builder().id(1).name("Item 1").description("Item 1 description").available(true)
                .build();
        UserDto booker = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();

        BookingDto expectedBookingDto = BookingDto.builder().id(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0)).status(BookingStatus.WAITING)
                .booker(booker).item(item).build();

        BookingDto actualBookingDto = json.readObject(bookingDto);

        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @SneakyThrows
    @Test
    void bookingDto_shouldSerialize() {
        ItemDto item = ItemDto.builder().id(1).name("Item 1").description("Item 1 description").available(true)
                .build();
        UserDto booker = UserDto.builder().id(1).name("User 1").email("user1@yandex.ru").build();

        BookingDto bookingDto = BookingDto.builder().id(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0)).status(BookingStatus.WAITING)
                .booker(booker).item(item).build();

        JsonContent<BookingDto> bookingDtoAsJson = json.write(bookingDto);

        assertThat(bookingDtoAsJson).isEqualToJson(ResourcePool.bookingDto);
    }
}
