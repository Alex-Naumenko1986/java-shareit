package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingStorage extends JpaRepository<BookingEntity, Integer> {
    List<BookingEntity> findByBooker_Id(Integer id, Pageable pageable);

    List<BookingEntity> findByBooker_IdAndStartBeforeAndEndAfter(Integer id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<BookingEntity> findByBooker_IdAndEndBefore(Integer id, LocalDateTime end, Pageable pageable);

    List<BookingEntity> findByBooker_IdAndStartAfter(Integer id, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findByBooker_IdAndStatus(Integer id, BookingStatus status, Pageable pageable);

    List<BookingEntity> findByItem_OwnerId(Integer ownerId, Pageable pageable);

    List<BookingEntity> findByItem_OwnerIdAndStartBeforeAndEndAfter(Integer ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<BookingEntity> findByItem_OwnerIdAndEndBefore(Integer ownerId, LocalDateTime end, Pageable pageable);

    List<BookingEntity> findByItem_OwnerIdAndStartAfter(Integer ownerId, LocalDateTime start, Pageable pageable);

    List<BookingEntity> findByItem_OwnerIdAndStatus(Integer ownerId, BookingStatus status, Pageable pageable);

    List<BookingEntity> findByItem_Id(Integer id);

    List<BookingEntity> findByItem_OwnerId(Integer ownerId);

    List<BookingEntity> findByItem_IdAndBooker_IdAndStatusAndEndBefore(Integer id, Integer id1, BookingStatus status, LocalDateTime end);

    List<BookingEntity> findByItem_IdAndStatusNotIn(Integer id, Collection<BookingStatus> statuses);


}
