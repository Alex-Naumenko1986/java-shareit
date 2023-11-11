package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<BookingEntity, Integer> {
    List<BookingEntity> findByBooker_IdOrderByStartDesc(Integer id);

    List<BookingEntity> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Integer id, LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findByBooker_IdAndEndBeforeOrderByStartDesc(Integer id, LocalDateTime end);

    List<BookingEntity> findByBooker_IdAndStartAfterOrderByStartDesc(Integer id, LocalDateTime start);

    List<BookingEntity> findByBooker_IdAndStatusOrderByStartDesc(Integer id, BookingStatus status);

    List<BookingEntity> findByItem_OwnerIdOrderByStartDesc(Integer ownerId);

    List<BookingEntity> findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer ownerId, LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findByItem_OwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);

    List<BookingEntity> findByItem_OwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime start);

    List<BookingEntity> findByItem_OwnerIdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status);

    List<BookingEntity> findByItem_Id(Integer id);

    List<BookingEntity> findByItem_OwnerId(Integer ownerId);

    List<BookingEntity> findByItem_IdAndBooker_IdAndStatusAndEndBefore(Integer id, Integer id1, BookingStatus status, LocalDateTime end);

    List<BookingEntity> findByItem_IdAndStatusNotIn(Integer id, Collection<BookingStatus> statuses);


}
