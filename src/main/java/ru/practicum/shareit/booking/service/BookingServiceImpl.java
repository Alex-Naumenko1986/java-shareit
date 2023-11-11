package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.ShortBookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.db.ItemStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.db.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ShortBookingMapper shortBookingMapper;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(ShortBookingDto bookingDto) {
        BookingEntity bookingEntity = shortBookingMapper.toEntity(bookingDto);
        checkBookingStartAndEndDate(bookingEntity);
        bookingEntity.setStatus(BookingStatus.WAITING);

        int bookerId = bookingEntity.getBooker().getId();
        UserEntity booker = userStorage.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found", bookerId)));
        bookingEntity.setBooker(booker);

        int itemId = bookingEntity.getItem().getId();

        ItemEntity item = itemStorage.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d was not found", itemId)));
        bookingEntity.setItem(item);

        if (bookingEntity.getItem().getOwnerId() == bookerId) {
            throw new BookingByOwnerException(String.format("Trying to book item with id: %d by the owner with id: %d",
                    itemId, bookerId));
        }

        if (!bookingEntity.getItem().getAvailable()) {
            throw new InvalidBookingOperationException(String.format("Trying to book item with id: %d. Item is " +
                    "unavailable", itemId));
        }

        bookingEntity = bookingStorage.save(bookingEntity);

        return bookingMapper.toDto(bookingEntity);
    }

    @Override
    public List<BookingDto> getUsersBookings(Integer userId, String state) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , userId)));

        State requestState;
        LocalDateTime now = LocalDateTime.now();
        try {
            requestState = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidBookingOperationException(String.format("Unknown state: %s", state));
        }
        List<BookingEntity> bookings;

        switch (requestState) {
            case ALL:
                bookings = bookingStorage.findByBooker_IdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc
                        (userId, now, now);
                break;
            case PAST:
                bookings = bookingStorage.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingStorage.findByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnersBookings(Integer userId, String state) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , userId)));

        State requestState;
        LocalDateTime now = LocalDateTime.now();

        try {
            requestState = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidBookingOperationException(String.format("Unknown state: %s", state));
        }

        List<BookingEntity> bookings;

        switch (requestState) {
            case ALL:
                bookings = bookingStorage.findByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingStorage.findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingStorage.findByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingStorage.findByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingStorage.findByItem_OwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingStorage.findByItem_OwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto getBooking(Integer userId, Integer bookingId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , userId)));

        BookingEntity booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Booking with id %d was not found", bookingId)));

        if (!Objects.equals(booking.getItem().getOwnerId(), userId) &&
                !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new UserNotFoundException(String.format("User with id: %d is trying to get info " +
                    "about booking with id: %d. This user is not the owner and not the booker and can't get info " +
                    "about this booking", userId, bookingId));
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approveBooking(Integer userId, Integer bookingId, Boolean isApproved) {
        BookingEntity booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Booking with id %d was not found", bookingId)));
        int ownerId = booking.getItem().getOwnerId();
        if (userId != ownerId) {
            throw new InvalidUserApprovesBookingException(String.format("User with id: %d is trying to change status " +
                    "of booking with id: %d. Operation failed. User is not the owner of item", ownerId, bookingId));
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidBookingOperationException(String.format("Trying to update status of booking with id: %d." +
                    "Status of this booking is not WAITING and can't be changed", bookingId));
        }

        if (isApproved == null) {
            throw new InvalidBookingOperationException(String.format("Trying to update status of booking with id: %d." +
                    "Approve parameter of request is not set", bookingId));
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        booking = bookingStorage.save(booking);
        return bookingMapper.toDto(booking);
    }

    private void checkBookingStartAndEndDate(BookingEntity bookingEntity) {
        if (bookingEntity.getStart() == null) {
            throw new InvalidBookingDatesException(String.format("Invalid start time of booking. " +
                    "Start time is null"));
        }

        if (bookingEntity.getEnd() == null) {
            throw new InvalidBookingDatesException(String.format("Invalid end time of booking. " +
                    "End time is null"));
        }

        if (!bookingEntity.getEnd().isAfter(bookingEntity.getStart())) {
            throw new InvalidBookingDatesException(String.format("Invalid booking time of booking. " +
                    "End time should go after start time"));
        }

        if (bookingEntity.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingDatesException(String.format("Invalid end time of booking. " +
                    "End time should be after current moment"));
        }

        if (bookingEntity.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingDatesException(String.format("Invalid start time of booking. " +
                    "Start time should be after current moment", bookingEntity.getId()));
        }
    }
}
