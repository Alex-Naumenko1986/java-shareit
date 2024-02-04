package ru.practicum.shareit.booking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;
import utils.ResourcePool;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.ResourcePool.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final EntityManager em;

    @Test
    void createBooking_shouldCreateBooking() {
        UserEntity owner = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(owner);
        em.persist(booker);
        em.flush();
        Integer ownerId = owner.getId();
        ItemEntity item = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(ownerId).build();
        em.persist(item);
        em.flush();
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().bookerId(booker.getId()).itemId(item.getId())
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 12, 0, 0)).build();

        BookingDto actualBookingDto = bookingService.createBooking(shortBookingDto);
        Integer bookingId = actualBookingDto.getId();

        TypedQuery<BookingEntity> query = em.createQuery("Select b from BookingEntity b where b.id = :id",
                BookingEntity.class);
        BookingEntity bookingEntityFromDb = query.setParameter("id", bookingId).getSingleResult();
        BookingEntity expectedBookingEntity = ResourcePool.read(bookingService_it_createBooking_bookingEntity,
                BookingEntity.class);

        Assertions.assertThat(expectedBookingEntity)
                .usingRecursiveComparison()
                .ignoringFields("id", "item", "booker")
                .isEqualTo(bookingEntityFromDb);
        Assertions.assertThat(expectedBookingEntity.getItem())
                .usingRecursiveComparison()
                .ignoringFields("id", "ownerId")
                .isEqualTo(bookingEntityFromDb.getItem());
        Assertions.assertThat(expectedBookingEntity.getBooker())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(bookingEntityFromDb.getBooker());
        assertNotNull(bookingEntityFromDb.getId());
        assertNotNull(bookingEntityFromDb.getItem().getId());
        assertNotNull(bookingEntityFromDb.getItem().getOwnerId());
        assertNotNull(bookingEntityFromDb.getBooker().getId());
    }

    @Test
    void getUsersBookings_shouldReturnAllBookings() {
        UserEntity owner = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(owner);
        em.persist(booker);
        em.flush();
        Integer ownerId = owner.getId();
        ItemEntity item = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(ownerId).build();
        em.persist(item);
        em.flush();

        BookingEntity bookingEntity1 = BookingEntity.builder().item(item).booker(booker)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0))
                .status(BookingStatus.WAITING).build();
        BookingEntity bookingEntity2 = BookingEntity.builder().item(item).booker(booker)
                .start(LocalDateTime.of(2024, 3, 1, 0, 0))
                .end(LocalDateTime.of(2024, 3, 11, 0, 0))
                .status(BookingStatus.WAITING).build();
        em.persist(bookingEntity1);
        em.persist(bookingEntity2);
        em.flush();

        List<BookingDto> actualBookingDtos = bookingService.getUsersBookings(booker.getId(), "all", 0, 20);
        List<BookingDto> expectedBookingDtos = ResourcePool.read(bookingService_it_getUsersBookings_bookingDto,
                new TypeReference<>() {
                });

        for (int i = 0; i < actualBookingDtos.size(); i++) {
            BookingDto expectedBookingDto = expectedBookingDtos.get(i);
            BookingDto actualBookingDto = actualBookingDtos.get(i);

            Assertions.assertThat(expectedBookingDto)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "item", "booker")
                    .isEqualTo(actualBookingDto);
            Assertions.assertThat(expectedBookingDto.getItem())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(actualBookingDto.getItem());
            Assertions.assertThat(expectedBookingDto.getBooker())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(actualBookingDto.getBooker());
            assertNotNull(actualBookingDto.getId());
            assertNotNull(actualBookingDto.getItem().getId());
            assertNotNull(actualBookingDto.getBooker().getId());
        }
    }

    @Test
    void getOwnersBookings_shouldReturnAllBookings() {
        UserEntity owner = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(owner);
        em.persist(booker);
        em.flush();
        Integer ownerId = owner.getId();
        ItemEntity item = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(ownerId).build();
        em.persist(item);
        em.flush();

        BookingEntity bookingEntity1 = BookingEntity.builder().item(item).booker(booker)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0))
                .status(BookingStatus.WAITING).build();
        BookingEntity bookingEntity2 = BookingEntity.builder().item(item).booker(booker)
                .start(LocalDateTime.of(2024, 3, 1, 0, 0))
                .end(LocalDateTime.of(2024, 3, 11, 0, 0))
                .status(BookingStatus.WAITING).build();
        em.persist(bookingEntity1);
        em.persist(bookingEntity2);
        em.flush();

        List<BookingDto> actualBookingDtos = bookingService.getOwnersBookings(owner.getId(), "all", 0, 20);
        List<BookingDto> expectedBookingDtos = ResourcePool.read(bookingService_it_getUsersBookings_bookingDto,
                new TypeReference<>() {
                });

        for (int i = 0; i < actualBookingDtos.size(); i++) {
            BookingDto expectedBookingDto = expectedBookingDtos.get(i);
            BookingDto actualBookingDto = actualBookingDtos.get(i);

            Assertions.assertThat(expectedBookingDto)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "item", "booker")
                    .isEqualTo(actualBookingDto);
            Assertions.assertThat(expectedBookingDto.getItem())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(actualBookingDto.getItem());
            Assertions.assertThat(expectedBookingDto.getBooker())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(actualBookingDto.getBooker());
            assertNotNull(actualBookingDto.getId());
            assertNotNull(actualBookingDto.getItem().getId());
            assertNotNull(actualBookingDto.getBooker().getId());
        }
    }

    @Test
    void getBooking_shouldReturnBooking() {
        UserEntity owner = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(owner);
        em.persist(booker);
        em.flush();
        Integer ownerId = owner.getId();
        ItemEntity item = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(ownerId).build();
        em.persist(item);
        em.flush();

        BookingEntity booking = BookingEntity.builder().item(item).booker(booker)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0))
                .status(BookingStatus.WAITING).build();
        em.persist(booking);
        em.flush();

        BookingDto actualBookingDto = bookingService.getBooking(ownerId, booking.getId());
        BookingDto expectedBookingDto = ResourcePool.read(bookingService_it_getBooking_bookingDto, BookingDto.class);

        Assertions.assertThat(expectedBookingDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "item", "booker")
                .isEqualTo(actualBookingDto);
        Assertions.assertThat(expectedBookingDto.getItem())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actualBookingDto.getItem());
        Assertions.assertThat(expectedBookingDto.getBooker())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actualBookingDto.getBooker());
        assertNotNull(actualBookingDto.getId());
        assertNotNull(actualBookingDto.getItem().getId());
        assertNotNull(actualBookingDto.getBooker().getId());
    }

    @Test
    void approveBooking_shouldChangeBookingStatusToApproved() {
        UserEntity owner = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(owner);
        em.persist(booker);
        em.flush();
        Integer ownerId = owner.getId();
        ItemEntity item = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(ownerId).build();
        em.persist(item);
        em.flush();

        BookingEntity booking = BookingEntity.builder().item(item).booker(booker)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 11, 0, 0))
                .status(BookingStatus.WAITING).build();
        em.persist(booking);
        em.flush();
        Integer bookingId = booking.getId();

        bookingService.approveBooking(ownerId, booking.getId(), true);

        TypedQuery<BookingEntity> query = em.createQuery("Select b from BookingEntity b where b.id = :id",
                BookingEntity.class);
        BookingEntity bookingEntityFromDb = query.setParameter("id", bookingId).getSingleResult();

        assertEquals(BookingStatus.APPROVED, bookingEntityFromDb.getStatus());
    }
}
