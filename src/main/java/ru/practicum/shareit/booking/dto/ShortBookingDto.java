package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ShortBookingDto {
    private Integer id;
    private Integer bookerId;
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
