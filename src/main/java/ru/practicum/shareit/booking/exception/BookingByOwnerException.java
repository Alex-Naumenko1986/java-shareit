package ru.practicum.shareit.booking.exception;

public class BookingByOwnerException extends RuntimeException {
    public BookingByOwnerException(String message) {
        super(message);
    }
}
