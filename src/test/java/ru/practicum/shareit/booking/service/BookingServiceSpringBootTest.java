package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.mapper.ShortBookingMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.UserStorage;
import utils.ResourcePool;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static utils.ResourcePool.*;

@SpringBootTest(classes = {BookingServiceImpl.class, BookingMapperImpl.class, ShortBookingMapper.class,
        ItemMapperImpl.class, UserMapperImpl.class})
public class BookingServiceSpringBootTest {
    @Autowired
    private BookingServiceImpl bookingService;
    @MockBean
    private BookingStorage bookingStorage;
    @MockBean
    private UserStorage userStorage;
    @MockBean
    private ItemStorage itemStorage;

    @Test
    void createBooking_shouldThrowExceptionWhenBookingStartTimeIsNull() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1).start(null)
                .end(LocalDateTime.of(2024, 2, 1, 0, 0)).build();

        InvalidBookingDatesException exception = assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Invalid start time of booking. Start time is null", exception.getMessage());
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingEndTimeIsNull() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 1, 10, 0))
                .end(null).build();

        InvalidBookingDatesException exception = assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Invalid end time of booking. End time is null", exception.getMessage());
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingEndTimeIsBeforeStartTime() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 1, 10, 0))
                .end(LocalDateTime.of(2024, 1, 30, 0, 0)).build();

        InvalidBookingDatesException exception = assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Invalid booking time of booking. End time should go after start time",
                exception.getMessage());
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingStartTimeIsBeforeNow() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2023, 12, 11, 10, 0))
                .end(LocalDateTime.of(2024, 1, 30, 0, 0)).build();

        InvalidBookingDatesException exception = assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Invalid start time of booking. Start time should be after current moment",
                exception.getMessage());
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingEndTimeIsBeforeNow() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2023, 12, 11, 10, 0))
                .end(LocalDateTime.of(2023, 12, 13, 0, 0)).build();

        InvalidBookingDatesException exception = assertThrows(InvalidBookingDatesException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Invalid end time of booking. End time should be after current moment",
                exception.getMessage());
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenUserNotFound() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 11, 10, 0))
                .end(LocalDateTime.of(2024, 2, 13, 0, 0)).build();

        when(userStorage.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("User with id 1 was not found",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenItemNotFound() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 11, 10, 0))
                .end(LocalDateTime.of(2024, 2, 13, 0, 0)).build();

        UserEntity booker = UserEntity.builder().id(1).name("User 1").email("user1@yandex.ru").build();

        when(userStorage.findById(1)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(1)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Item with id 1 was not found",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(itemStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenBookingCreatedByItemOwner() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 11, 10, 0))
                .end(LocalDateTime.of(2024, 2, 13, 0, 0)).build();

        UserEntity booker = UserEntity.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        ItemEntity item = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).ownerId(1).build();

        when(userStorage.findById(1)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(1)).thenReturn(Optional.of(item));

        BookingByOwnerException exception = assertThrows(BookingByOwnerException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Trying to book item with id: 1 by the owner with id: 1",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(itemStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenItemIsNotAvailable() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 11, 10, 0))
                .end(LocalDateTime.of(2024, 2, 13, 0, 0)).build();

        UserEntity booker = UserEntity.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        ItemEntity item = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(false).ownerId(2).build();

        when(userStorage.findById(1)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(1)).thenReturn(Optional.of(item));

        InvalidBookingOperationException exception = assertThrows(InvalidBookingOperationException.class,
                () -> bookingService.createBooking(shortBookingDto));

        assertEquals("Trying to book item with id: 1. Item is unavailable",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(itemStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void createBooking_shouldCreateBooking() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(1).itemId(1)
                .start(LocalDateTime.of(2024, 2, 11, 10, 0))
                .end(LocalDateTime.of(2024, 2, 13, 0, 0)).build();

        UserEntity booker = UserEntity.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        ItemEntity item = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).ownerId(2).build();

        when(userStorage.findById(1)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(1)).thenReturn(Optional.of(item));
        when(bookingStorage.save(any()))
                .thenAnswer(invocationOnMock -> {
                    BookingEntity bookingEntity = invocationOnMock.getArgument(0, BookingEntity.class);
                    bookingEntity.setId(1);
                    return bookingEntity;
                });

        BookingDto actualBookingDto = bookingService.createBooking(shortBookingDto);
        BookingDto expectedBookingDto = ResourcePool.read(bookingService_sb_createBooking_bookingDto, BookingDto.class);

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(itemStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1)).save(any());
        verifyNoMoreInteractions(userStorage, itemStorage, bookingStorage);
    }

    @Test
    void getUsersBookings_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getUsersBookings(1, "waiting", 0, 20));

        assertEquals("User with id 1 was not found",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldThrowExceptionWhenStatusIsInvalid() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));

        InvalidBookingOperationException exception = assertThrows(InvalidBookingOperationException.class,
                () -> bookingService.getUsersBookings(1, "unknown", 0, 20));

        assertEquals("Unknown state: unknown",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldReturnAllBookings() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_all, new TypeReference<>() {
                });
        when(bookingStorage.findByBooker_Id(eq(1), any())).thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getUsersBookings(1, "all", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_all, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1)).findByBooker_Id(eq(1), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldReturnCurrentBookings() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_current, new TypeReference<>() {
                });
        when(bookingStorage.findByBooker_IdAndStartBeforeAndEndAfter(eq(1), any(), any(), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getUsersBookings(1, "current", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_current, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1))
                .findByBooker_IdAndStartBeforeAndEndAfter(eq(1), any(), any(), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldReturnPastBookings() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_past, new TypeReference<>() {
                });
        when(bookingStorage.findByBooker_IdAndEndBefore(eq(1), any(), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getUsersBookings(1, "past", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_past, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1))
                .findByBooker_IdAndEndBefore(eq(1), any(), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldReturnFutureBookings() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_future, new TypeReference<>() {
                });
        when(bookingStorage.findByBooker_IdAndStartAfter(eq(1), any(), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getUsersBookings(1, "future", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_future, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1))
                .findByBooker_IdAndStartAfter(eq(1), any(), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldReturnWaitingBookings() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_future, new TypeReference<>() {
                });
        when(bookingStorage.findByBooker_IdAndStatus(eq(1), eq(BookingStatus.WAITING), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getUsersBookings(1, "waiting", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_future, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1))
                .findByBooker_IdAndStatus(eq(1), eq(BookingStatus.WAITING), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getUsersBookings_shouldReturnRejectedBookings() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_rejected, new TypeReference<>() {
                });
        when(bookingStorage.findByBooker_IdAndStatus(eq(1), eq(BookingStatus.REJECTED), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getUsersBookings(1, "rejected", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_rejected, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1))
                .findByBooker_IdAndStatus(eq(1), eq(BookingStatus.REJECTED), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(2)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getOwnersBookings(2, "waiting", 0, 20));

        assertEquals("User with id 2 was not found",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(2);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldThrowExceptionWhenStatusIsInvalid() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));

        InvalidBookingOperationException exception = assertThrows(InvalidBookingOperationException.class,
                () -> bookingService.getOwnersBookings(2, "unknown", 0, 20));

        assertEquals("Unknown state: unknown",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(2);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldReturnAllBookings() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_all, new TypeReference<>() {
                });
        when(bookingStorage.findByItem_OwnerId(eq(2), any())).thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getOwnersBookings(2, "all", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_all, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(2);
        verify(bookingStorage, Mockito.times(1)).findByItem_OwnerId(eq(2), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldReturnCurrentBookings() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_current, new TypeReference<>() {
                });
        when(bookingStorage.findByItem_OwnerIdAndStartBeforeAndEndAfter(eq(2), any(), any(), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getOwnersBookings(2, "current", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_current, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(2);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_OwnerIdAndStartBeforeAndEndAfter(eq(2), any(), any(), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldReturnPastBookings() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_past, new TypeReference<>() {
                });
        when(bookingStorage.findByItem_OwnerIdAndEndBefore(eq(2), any(), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getOwnersBookings(2, "past", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_past, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(2);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_OwnerIdAndEndBefore(eq(2), any(), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldReturnFutureBookings() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_future, new TypeReference<>() {
                });
        when(bookingStorage.findByItem_OwnerIdAndStartAfter(eq(2), any(), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getOwnersBookings(2, "future", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_future, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(2);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_OwnerIdAndStartAfter(eq(2), any(), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldReturnWaitingBookings() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_future, new TypeReference<>() {
                });
        when(bookingStorage.findByItem_OwnerIdAndStatus(eq(2), eq(BookingStatus.WAITING), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getOwnersBookings(2, "waiting", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_future, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(2);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_OwnerIdAndStatus(eq(2), eq(BookingStatus.WAITING), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getOwnersBookings_shouldReturnRejectedBookings() {
        when(userStorage.findById(2)).thenReturn(Optional.of(new UserEntity()));
        List<BookingEntity> bookingEntities = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingEntity_rejected, new TypeReference<>() {
                });
        when(bookingStorage.findByItem_OwnerIdAndStatus(eq(2), eq(BookingStatus.REJECTED), any()))
                .thenReturn(bookingEntities);

        List<BookingDto> actualBookingDto = bookingService.getOwnersBookings(2, "rejected", 0, 20);
        List<BookingDto> expectedBookingDto = ResourcePool
                .read(bookingService_sb_getUsersBookings_bookingDto_rejected, new TypeReference<>() {
                });

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(2);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_OwnerIdAndStatus(eq(2), eq(BookingStatus.REJECTED), any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void getBooking_shouldThrowExceptionWhenUserNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBooking(1, 1));

        assertEquals("User with id 1 was not found",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verifyNoInteractions(bookingStorage);
    }

    @Test
    void getBooking_shouldThrowExceptionWhenBookingNotFound() {
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        when(bookingStorage.findById(1)).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(1, 1));

        assertEquals("Booking with id 1 was not found",
                exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1)).findById(1);
    }

    @Test
    void getBooking_shouldThrowExceptionWhenBookingRequestedByNotOwnerAndNotBooker() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        when(userStorage.findById(3)).thenReturn(Optional.of(new UserEntity()));
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBooking(3, 1));

        assertEquals("User with id: 3 is trying to get info " +
                "about booking with id: 1. This user is not the owner and not the booker and can't get info " +
                "about this booking", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(3);
        verify(bookingStorage, Mockito.times(1)).findById(1);
    }

    @Test
    void getBooking_shouldReturnBooking() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        when(userStorage.findById(1)).thenReturn(Optional.of(new UserEntity()));
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));

        BookingDto actualBookingDto = bookingService.getBooking(1, 1);
        BookingDto expectedBookingDto = ResourcePool.read(bookingService_sb_getBooking_bookingDto, BookingDto.class);

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(userStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1)).findById(1);
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenBookingNotFound() {
        when(bookingStorage.findById(1)).thenReturn(Optional.empty());

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(1, 1, true));

        assertEquals("Booking with id 1 was not found",
                exception.getMessage());
        verify(bookingStorage, Mockito.times(1)).findById(1);
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenApproveNotByOwner() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));

        InvalidUserApprovesBookingException exception = assertThrows(InvalidUserApprovesBookingException.class,
                () -> bookingService.approveBooking(1, 1, true));

        assertEquals("User with id: 1 is trying to change status " +
                        "of booking with id: 1. Operation failed. User is not the owner of item",
                exception.getMessage());
        verify(bookingStorage, Mockito.times(1)).findById(1);
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenBookingStatusIsNotWaiting() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        bookingEntity.setStatus(BookingStatus.REJECTED);
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));

        InvalidBookingOperationException exception = assertThrows(InvalidBookingOperationException.class,
                () -> bookingService.approveBooking(2, 1, true));

        assertEquals("Trying to update status of booking with id: 1." +
                "Status of this booking is not WAITING and can't be changed", exception.getMessage());
        verify(bookingStorage, Mockito.times(1)).findById(1);
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void approveBooking_shouldThrowExceptionWhenIsApprovedNull() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));

        InvalidBookingOperationException exception = assertThrows(InvalidBookingOperationException.class,
                () -> bookingService.approveBooking(2, 1, null));

        assertEquals("Trying to update status of booking with id: 1." +
                "Approve parameter of request is not set", exception.getMessage());
        verify(bookingStorage, Mockito.times(1)).findById(1);
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void approveBooking_shouldUpdateBookingStatusToApproved() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));
        when(bookingStorage.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, BookingEntity.class));

        BookingDto actualBookingDto = bookingService.approveBooking(2, 1, true);
        BookingDto expectedBookingDto = ResourcePool.read(bookingService_sb_getBooking_bookingDto, BookingDto.class);
        expectedBookingDto.setStatus(BookingStatus.APPROVED);

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(bookingStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1)).save(any());
        verifyNoMoreInteractions(bookingStorage);
    }

    @Test
    void approveBooking_shouldUpdateBookingStatusToRejected() {
        BookingEntity bookingEntity = ResourcePool.read(bookingService_sb_getBooking_bookingEntity, BookingEntity.class);
        when(bookingStorage.findById(1)).thenReturn(Optional.of(bookingEntity));
        when(bookingStorage.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, BookingEntity.class));

        BookingDto actualBookingDto = bookingService.approveBooking(2, 1, false);
        BookingDto expectedBookingDto = ResourcePool.read(bookingService_sb_getBooking_bookingDto, BookingDto.class);
        expectedBookingDto.setStatus(BookingStatus.REJECTED);

        assertEquals(expectedBookingDto, actualBookingDto);
        verify(bookingStorage, Mockito.times(1)).findById(1);
        verify(bookingStorage, Mockito.times(1)).save(any());
        verifyNoMoreInteractions(bookingStorage);
    }
}
