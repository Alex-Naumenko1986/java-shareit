package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.exception.BookingByOwnerException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidBookingOperationException;
import ru.practicum.shareit.booking.exception.InvalidUserApprovesBookingException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.exception.IllegalAddCommentOperationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class,
        ItemRequestController.class})
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.error("Error occurred. Validation failed:{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidBookingOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBookingOperationException(InvalidBookingOperationException e) {
        log.error("Error occurred. Invalid operation with booking:{}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUnsupportedOperationException(UnsupportedOperationException e) {
        log.error("Error occurred. User, who is not the owner of the item, is trying to update the item: {}",
                e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.error("Error occurred. User not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidUserApprovesBookingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInvalidUserApprovesBookingException(InvalidUserApprovesBookingException e) {
        log.error("Error occurred. User who is not the owner of booking is trying to change status of booking:" +
                " {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BookingByOwnerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingByOwnerException(BookingByOwnerException e) {
        log.error("Error occurred. Owner of the item is trying to book the item:" +
                " {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(BookingNotFoundException e) {
        log.error("Error occurred. Booking not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(IllegalAddCommentOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalAddCommentOperationException(IllegalAddCommentOperationException e) {
        log.error("Error occurred. Failed to add comment: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException e) {
        log.error("Error occurred. Item not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ItemRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestNotFoundException(ItemRequestNotFoundException e) {
        log.error("Error occurred. Item request not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(Throwable t) {
        log.error("Error occurred", t);
        return new ErrorResponse(t.getMessage());
    }
}
