package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(int userId, Item item);

    Item getItemById(int id);

    List<Item> getOwnersItems(int ownerId);

    List<Item> searchItems(String query);
}
