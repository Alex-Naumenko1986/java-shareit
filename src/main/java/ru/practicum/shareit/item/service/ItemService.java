package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item updateItem(int userId, Item item);

    Item getItemById(int id);

    List<Item> getOwnersItems(int ownerId);

    List<Item> searchItems(String query);
}
