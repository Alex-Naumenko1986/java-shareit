package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

@Component
public class ShortBookingMapper {
    public BookingEntity toEntity(ShortBookingDto bookingDto) {
        return BookingEntity.builder().start(bookingDto.getStart()).end(bookingDto.getEnd())
                .status(BookingStatus.WAITING).item(ItemEntity.builder().id(bookingDto.getItemId()).build())
                .booker(UserEntity.builder().id(bookingDto.getBookerId()).build()).build();
    }

    public ShortBookingDto toDto(BookingEntity bookingEntity) {
        return ShortBookingDto.builder().id(bookingEntity.getId()).start(bookingEntity.getStart())
                .end(bookingEntity.getEnd()).itemId(bookingEntity.getItem().getId())
                .bookerId(bookingEntity.getBooker().getId()).build();
    }
}
