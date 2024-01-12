package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                     @RequestBody @Valid ItemRequestDto itemRequestDto) {
        ItemRequestDto createdRequestDto = itemRequestService.createItemRequest(userId, itemRequestDto);
        log.info("Created new item request with id: {} by the requestor with id: {}, {}",
                createdRequestDto.getId(), userId, createdRequestDto);
        return createdRequestDto;
    }

    @GetMapping
    public List<ItemResponseDto> getUsersItemRequests(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemRequestService.getUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                          @PathVariable("requestId") Integer requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

}
