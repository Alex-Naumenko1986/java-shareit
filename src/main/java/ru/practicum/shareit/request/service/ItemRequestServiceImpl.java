package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.mapper.ShortItemMapper;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.pageable.CustomPageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemResponseMapper;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemRequestMapper itemRequestMapper;
    private final ShortItemMapper shortItemMapper;
    private final ItemResponseMapper itemResponseMapper;

    @Override
    public ItemRequestDto createItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));
        ItemRequestEntity itemRequestEntity = ItemRequestEntity.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now()).requestorId(userId).build();
        itemRequestEntity = itemRequestStorage.save(itemRequestEntity);
        ItemRequestDto createdItemRequest = itemRequestMapper.toDto(itemRequestEntity);
        log.info("Created new item request: {}", createdItemRequest);
        return createdItemRequest;
    }

    @Override
    public List<ItemResponseDto> getUsersItemRequests(Integer userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));

        List<ItemRequestEntity> userRequests = itemRequestStorage.findByRequestorIdOrderByCreatedDesc(userId);
        List<ItemResponseDto> responses = new ArrayList<>();
        for (ItemRequestEntity request : userRequests) {
            List<ItemEntity> items = itemStorage.findByRequestId(request.getId());
            List<ShortItemDto> itemDtos = items.stream().map(shortItemMapper::toDto)
                    .collect(Collectors.toList());
            ItemResponseDto itemResponseDto = itemResponseMapper.toDto(request);
            itemResponseDto.setItems(itemDtos);
            responses.add(itemResponseDto);
        }
        return responses;
    }

    @Override
    public ItemResponseDto getRequestById(Integer userId, Integer requestId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));

        ItemRequestEntity request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String.format("Item request with id %d was " +
                        "not found", requestId)));
        List<ItemEntity> items = itemStorage.findByRequestId(request.getId());

        ItemResponseDto itemResponseDto = itemResponseMapper.toDto(request);
        List<ShortItemDto> shortItemDtos = items.stream().map(shortItemMapper::toDto).collect(Collectors.toList());
        itemResponseDto.setItems(shortItemDtos);
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> getAllRequests(Integer userId, Integer from, Integer size) {
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found",
                        userId)));

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = new CustomPageable(from, size, sort);
        List<ItemRequestEntity> itemRequests = itemRequestStorage.findByRequestorIdNot(userId, pageable);

        List<ItemResponseDto> responses = new ArrayList<>();
        for (ItemRequestEntity request : itemRequests) {
            List<ItemEntity> items = itemStorage.findByRequestId(request.getId());
            List<ShortItemDto> itemDtos = items.stream().map(shortItemMapper::toDto)
                    .collect(Collectors.toList());
            ItemResponseDto itemResponseDto = itemResponseMapper.toDto(request);
            itemResponseDto.setItems(itemDtos);
            responses.add(itemResponseDto);
        }
        return responses;
    }
}
