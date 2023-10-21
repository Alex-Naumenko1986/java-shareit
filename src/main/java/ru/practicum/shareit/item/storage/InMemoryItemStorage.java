package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.id_generator.IdGenerator;
import ru.practicum.shareit.item.exception.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final IdGenerator idGenerator;
    private Map<Integer, Item> idToItem = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        if (idToItem.containsKey(item.getId())) {
            throw new ItemAlreadyExistsException(String.format("Item with id: %d already exists", item.getId()));
        }
        int id = idGenerator.generateId();
        item.setId(id);
        idToItem.put(id, item);
        log.info("New item added to storage: {}", item);
        return item;
    }

    @Override
    public Item updateItem(int userId, Item item) {
        if (idToItem.get(item.getId()) == null) {
            throw new ItemNotFoundException(String.format("Item with id: %d not found", item.getId()));
        }
        Item itemFromStorage = idToItem.get(item.getId());
        if (itemFromStorage.getOwnerId() != userId) {
            throw new UnsupportedOperationException(String.format("User with id: %d is not owner of the item " +
                    "with id: %d", userId, item.getId()));
        }
        updateItemFields(itemFromStorage, item);
        idToItem.put(item.getId(), itemFromStorage);
        log.info("Item was updated in storage: {}", itemFromStorage);
        return itemFromStorage;
    }

    @Override
    public Item getItemById(int id) {
        if (idToItem.get(id) == null) {
            throw new ItemNotFoundException(String.format("Item with id: %d not found", id));
        }
        return idToItem.get(id);
    }

    @Override
    public List<Item> getOwnersItems(int ownerId) {
        return idToItem.values().stream().filter(item -> item.getOwnerId() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String query) {
        if (query.isEmpty() || query.isBlank()) {
            return new ArrayList<>();
        }
        return idToItem.values().stream().filter(item -> item.getAvailable() &&
                (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()))).collect(Collectors.toList());
    }

    private void updateItemFields(Item itemFromStorage, Item item) {
        if (item.getName() != null && !item.getName().isBlank()) {
            itemFromStorage.setName(item.getName());
        }

        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemFromStorage.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemFromStorage.setAvailable(item.getAvailable());
        }
    }
}
