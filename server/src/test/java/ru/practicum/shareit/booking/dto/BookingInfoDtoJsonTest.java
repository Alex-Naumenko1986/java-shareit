package ru.practicum.shareit.booking.dto;

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
import static utils.ResourcePool.bookingInfoDto;

@JsonTest
public class BookingInfoDtoJsonTest {
    @Autowired
    private JacksonTester<BookingInfoDto> json;

    @SneakyThrows
    @Test
    void bookingInfoDto_shouldDeserialize() {
        BookingInfoDto expectedBookingInfoDto = BookingInfoDto.builder().id(1).bookerId(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0)).build();

        BookingInfoDto actualBookingInfoDto = json.readObject(bookingInfoDto);

        assertEquals(expectedBookingInfoDto, actualBookingInfoDto);
    }

    @SneakyThrows
    @Test
    void bookingInfoDto_shouldSerialize() {
        BookingInfoDto bookingInfoDto = BookingInfoDto.builder().id(1).bookerId(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0)).build();

        JsonContent<BookingInfoDto> bookingInfoDtoAsJson = json.write(bookingInfoDto);

        assertThat(bookingInfoDtoAsJson).isEqualToJson(ResourcePool.bookingInfoDto);
    }
}
