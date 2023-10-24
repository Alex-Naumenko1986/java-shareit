package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public Item createItem(Item item) {
        userStorage.getUserById(item.getOwnerId());
        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(int userId, Item item) {
        userStorage.getUserById(userId);
        return itemStorage.updateItem(userId, item);
    }

    @Override
    public Item getItemById(int id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public List<Item> getOwnersItems(int ownerId) {
        userStorage.getUserById(ownerId);
        return itemStorage.getOwnersItems(ownerId);
    }

    @Override
    public List<Item> searchItems(String query) {
        return itemStorage.searchItems(query);
    }
}
