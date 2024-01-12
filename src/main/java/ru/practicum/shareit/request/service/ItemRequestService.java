package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemResponseDto> getUsersItemRequests(Integer userId);

    ItemResponseDto getRequestById(Integer userId, Integer requestId);

    List<ItemResponseDto> getAllRequests(Integer userId, Integer from, Integer size);
}
