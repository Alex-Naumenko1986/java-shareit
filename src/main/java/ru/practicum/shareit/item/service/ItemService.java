package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(int ownerId, ItemDto itemDto);

    ItemDto updateItem(int userId, ItemDto itemDto);

    ItemDto getItemById(int userId, int itemId);

    List<ItemDto> getOwnersItems(int ownerId);

    List<ItemDto> searchItems(String query);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto);
}
