package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

@Repository
public interface ItemStorage extends JpaRepository<ItemEntity, Integer> {
    List<ItemEntity> findByRequestId(Integer requestId);

    @Query("select i from ItemEntity i where i.ownerId = ?1")
    List<ItemEntity> findByOwnerId(Integer ownerId, Pageable pageable);

    @Query("select i from ItemEntity i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) or lower(i.description) " +
            "like lower(concat('%', ?1, '%'))) and i.available=true")
    List<ItemEntity> searchItems(String query, Pageable pageable);
}
