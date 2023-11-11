package ru.practicum.shareit.item.storage.in_memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.id_generator.IdGenerator;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemStorageImpl implements InMemoryItemStorage {
    private final IdGenerator idGenerator;
    private Map<Integer, ItemEntity> idToItem = new HashMap<>();

    @Override
    public ItemEntity createItem(ItemEntity itemEntity) {
        int id = idGenerator.generateId();
        itemEntity.setId(id);
        idToItem.put(id, itemEntity);
        log.info("New item with id: {} added to storage: {}", itemEntity.getId(), itemEntity);
        return itemEntity;
    }

    @Override
    public ItemEntity updateItem(int userId, ItemEntity itemEntity) {
        if (idToItem.get(itemEntity.getId()) == null) {
            throw new ItemNotFoundException(String.format("Item with id: %d not found", itemEntity.getId()));
        }
        ItemEntity itemEntityFromStorage = idToItem.get(itemEntity.getId());
        if (itemEntityFromStorage.getOwnerId() != userId) {
            throw new UnsupportedOperationException(String.format("User with id: %d is not owner of the item " +
                    "with id: %d", userId, itemEntity.getId()));
        }
        updateItemFields(itemEntityFromStorage, itemEntity);
        idToItem.put(itemEntity.getId(), itemEntityFromStorage);
        log.info("Item with id: {} was updated in storage: {}", itemEntityFromStorage.getId(), itemEntityFromStorage);
        return itemEntityFromStorage;
    }

    @Override
    public ItemEntity getItemById(int id) {
        if (idToItem.get(id) == null) {
            throw new ItemNotFoundException(String.format("Item with id: %d not found", id));
        }
        return idToItem.get(id);
    }

    @Override
    public List<ItemEntity> getOwnersItems(int ownerId) {
        return idToItem.values().stream().filter(item -> item.getOwnerId() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<ItemEntity> searchItems(String query) {
        if (query.isEmpty() || query.isBlank()) {
            return new ArrayList<>();
        }
        return idToItem.values().stream().filter(item -> item.getAvailable() &&
                (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()))).collect(Collectors.toList());
    }

    private void updateItemFields(ItemEntity itemEntityFromStorage, ItemEntity itemEntity) {
        if (itemEntity.getName() != null && !itemEntity.getName().isBlank()) {
            itemEntityFromStorage.setName(itemEntity.getName());
        }

        if (itemEntity.getDescription() != null && !itemEntity.getDescription().isBlank()) {
            itemEntityFromStorage.setDescription(itemEntity.getDescription());
        }

        if (itemEntity.getAvailable() != null) {
            itemEntityFromStorage.setAvailable(itemEntity.getAvailable());
        }
    }
}
