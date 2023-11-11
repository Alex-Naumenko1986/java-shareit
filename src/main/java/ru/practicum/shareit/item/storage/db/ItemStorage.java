package ru.practicum.shareit.item.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

public interface ItemStorage extends JpaRepository<ItemEntity, Integer> {
    @Query("select i from ItemEntity i where i.ownerId = ?1")
    List<ItemEntity> findByOwnerId(Integer ownerId);

    @Query("select i from ItemEntity i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) or lower(i.description) " +
            "like lower(concat('%', ?1, '%'))) and i.available=true")
    List<ItemEntity> searchItems(String query);
}
