package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody @Valid ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        ItemDto createdItemDto = itemMapper.toItemDto(itemService.createItem(item));
        log.info("Created new item {}", createdItemDto);
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int itemId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        Item item = itemMapper.toItem(itemDto);
        ItemDto updatedItemDto = itemMapper.toItemDto(itemService.updateItem(userId, item));
        log.info("Updated item: {}", updatedItemDto);
        return updatedItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int itemId) {
        return itemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getOwnersItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        List<Item> ownersItems = itemService.getOwnersItems(userId);
        return ownersItems.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @RequestParam("text") String text) {
        List<Item> items = itemService.searchItems(text);
        return items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }
}
