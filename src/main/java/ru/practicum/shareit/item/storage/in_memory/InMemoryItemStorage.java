package ru.practicum.shareit.item.storage.in_memory;

import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

public interface InMemoryItemStorage {
    ItemEntity createItem(ItemEntity itemEntity);

    ItemEntity updateItem(int userId, ItemEntity itemEntity);

    ItemEntity getItemById(int id);

    List<ItemEntity> getOwnersItems(int ownerId);

    List<ItemEntity> searchItems(String query);
}
