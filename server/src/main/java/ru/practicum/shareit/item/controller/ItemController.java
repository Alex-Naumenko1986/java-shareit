package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemDto itemDto) {
        ItemDto createdItemDto = itemService.createItem(userId, itemDto);
        log.info("Created new item with id: {} by the owner with id: {}, {}", createdItemDto.getId(), userId,
                createdItemDto);
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int itemId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        ItemDto updatedItemDto = itemService.updateItem(userId, itemDto);
        log.info("Updated item with id: {} by the owner with id: {}, {}", updatedItemDto.getId(), userId,
                updatedItemDto);
        return updatedItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") int itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnersItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestParam(name = "from") Integer from,
                                        @RequestParam(name = "size") Integer size) {
        return itemService.getOwnersItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @RequestParam("text") String text,
                                     @RequestParam(name = "from") Integer from,
                                     @RequestParam(name = "size") Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable("itemId") Integer itemId,
                                 @RequestBody CommentDto commentDto) {
        CommentDto addedComment = itemService.addComment(userId, itemId, commentDto);
        log.info("New comment was added to item with id: {}, comment: {}", itemId, addedComment);
        return addedComment;
    }
}
