package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ShortBookingDto {
    private Integer id;
    private Integer bookerId;
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
