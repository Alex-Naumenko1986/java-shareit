package ru.practicum.shareit.booking.exception;

public class InvalidUserApprovesBookingException extends RuntimeException {
    public InvalidUserApprovesBookingException(String message) {
        super(message);
    }
}
