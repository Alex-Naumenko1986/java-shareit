package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.BookingEntity;

@Component
public class BookingInfoMapper {
    public BookingInfoDto toDto(BookingEntity bookingEntity) {
        return BookingInfoDto.builder().id(bookingEntity.getId()).bookerId(bookingEntity.getBooker().getId())
                .start(bookingEntity.getStart()).end(bookingEntity.getEnd()).build();
    }
}
