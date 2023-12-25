package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.List;


public interface BookingService {
    BookingDto createBooking(ShortBookingDto bookingDto);

    BookingDto approveBooking(Integer userId, Integer bookingId, Boolean isApproved);

    BookingDto getBooking(Integer userId, Integer bookingId);

    List<BookingDto> getUsersBookings(Integer userId, String state, int from, int size);

    List<BookingDto> getOwnersBookings(Integer userId, String state, int from, int size);
}
