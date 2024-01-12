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
import static utils.ResourcePool.shortBookingDto;

@JsonTest
public class ShortBookingDtoJsonTest {
    @Autowired
    private JacksonTester<ShortBookingDto> json;

    @SneakyThrows
    @Test
    void shortBookingDto_shouldDeserialize() {
        ShortBookingDto expectedShortBookingDto = ShortBookingDto.builder().id(1).itemId(1).bookerId(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0)).build();

        ShortBookingDto actualShortBookingDto = json.readObject(shortBookingDto);

        assertEquals(expectedShortBookingDto, actualShortBookingDto);
    }

    @SneakyThrows
    @Test
    void shortBookingDto_shouldSerialize() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().id(1).itemId(1).bookerId(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0)).build();

        JsonContent<ShortBookingDto> shortBookingDtoAsJson = json.write(shortBookingDto);

        assertThat(shortBookingDtoAsJson).isEqualToJson(ResourcePool.shortBookingDto);
    }
}
