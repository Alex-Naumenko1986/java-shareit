package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating new item: {} by the owner with id: {}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                             @PathVariable("itemId") @Min(1) int itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item with id: {} by the owner with id: {}, {}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                              @PathVariable("itemId") @Min(1) int itemId) {
        log.info("Getting item by id={} by the user with id={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnersItems(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get owners items, owner id={}, from={}, size={}", userId, from, size);
        return itemClient.getOwnersItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                              @RequestParam("text") String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items with text={}, user id={}, from={}, size={}", text, userId, from, size);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") @Min(1) int userId,
                                             @PathVariable("itemId") @Min(1) Integer itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Add new comment by user with id={} to item with id={}, comment: {}", userId, itemId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
