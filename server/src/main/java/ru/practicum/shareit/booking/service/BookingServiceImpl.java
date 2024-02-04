package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.exception.BookingByOwnerException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidBookingOperationException;
import ru.practicum.shareit.booking.exception.InvalidUserApprovesBookingException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.ShortBookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.pageable.CustomPageable;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ShortBookingMapper shortBookingMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(ShortBookingDto bookingDto) {
        BookingEntity bookingEntity = shortBookingMapper.toEntity(bookingDto);
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

        log.info("New booking was created: {}", bookingEntity);

        return bookingMapper.toDto(bookingEntity);
    }

    @Override
    @Transactional
    public List<BookingDto> getUsersBookings(Integer userId, String state, int from, int size) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));

        State requestState = State.valueOf(state.toUpperCase());
        ;
        LocalDateTime now = LocalDateTime.now();

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = new CustomPageable(from, size, sort);

        List<BookingEntity> bookings;

        switch (requestState) {
            case ALL:
                bookings = bookingStorage.findByBooker_Id(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBooker_IdAndStartBeforeAndEndAfter(
                        userId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findByBooker_IdAndEndBefore(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findByBooker_IdAndStartAfter(userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDto> getOwnersBookings(Integer userId, String state, int from, int size) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));

        State requestState = State.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = new CustomPageable(from, size, sort);

        List<BookingEntity> bookings;

        switch (requestState) {
            case ALL:
                bookings = bookingStorage.findByItem_OwnerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingStorage.findByItem_OwnerIdAndStartBeforeAndEndAfter(userId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingStorage.findByItem_OwnerIdAndEndBefore(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingStorage.findByItem_OwnerIdAndStartAfter(userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingStorage.findByItem_OwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingStorage.findByItem_OwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto getBooking(Integer userId, Integer bookingId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));

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
    @Transactional
    public BookingDto approveBooking(Integer userId, Integer bookingId, Boolean isApproved) {
        BookingEntity booking = bookingStorage.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Booking with id %d was not found", bookingId)));
        int ownerId = booking.getItem().getOwnerId();
        if (userId != ownerId) {
            throw new InvalidUserApprovesBookingException(String.format("User with id: %d is trying to change status " +
                    "of booking with id: %d. Operation failed. User is not the owner of item", userId, bookingId));
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidBookingOperationException(String.format("Trying to update status of booking with id: %d." +
                    "Status of this booking is not WAITING and can't be changed", bookingId));
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        booking = bookingStorage.save(booking);
        log.info("Status of booking was changed: {}", booking);
        return bookingMapper.toDto(booking);
    }
}
