package ru.practicum.shareit.request.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.item.mapper.ShortItemMapperImpl;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.mapper.ItemResponseMapperImpl;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.UserStorage;
import utils.ResourcePool;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static utils.ResourcePool.itemRequestService_sb_getRequestById_itemResponseDto;
import static utils.ResourcePool.itemRequestService_sb_getUsersItemRequests_itemResponseDto;

@SpringBootTest(classes = {ItemRequestServiceImpl.class, ItemRequestMapperImpl.class, ItemResponseMapperImpl.class,
        ShortItemMapperImpl.class})
public class ItemRequestServiceSpringBootTest {
    @Autowired
    private ItemRequestServiceImpl itemRequestService;
    @MockBean
    private UserStorage userStorage;
    @MockBean
    private ItemStorage itemStorage;
    @MockBean
    private ItemRequestStorage itemRequestStorage;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = ItemRequestDto.builder().description("Description").build();
    }

    @Test
    void createItemRequest_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, itemRequestDto));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoInteractions(itemRequestStorage);
    }

    @Test
    void createItemRequest_shouldCreateItemRequest() {
        Integer userId = 1;
        ItemRequestEntity itemRequestEntity = ItemRequestEntity.builder().id(1).description("Description")
                .requestorId(1).created(LocalDateTime.of(2023, 12, 8, 0, 0)).build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemRequestStorage.save(any())).thenReturn(itemRequestEntity);

        ItemRequestDto actualItemRequestDto = itemRequestService.createItemRequest(1, itemRequestDto);

        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder().id(1).description("Description")
                .requestorId(1).created(LocalDateTime.of(2023, 12, 8, 0, 0)).build();
        assertEquals(expectedItemRequestDto, actualItemRequestDto);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemRequestStorage, Mockito.times(1)).save(any());
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemRequestStorage);
    }

    @Test
    void getUsersItemRequests_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getUsersItemRequests(userId));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoInteractions(itemRequestStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void getUsersItemRequests_shouldReturnItemRequests() {
        Integer userId = 1;
        ItemRequestEntity itemRequestEntity = ItemRequestEntity.builder().id(1).description("Description")
                .requestorId(1).created(LocalDateTime.of(2023, 12, 8, 0, 0))
                .build();
        ItemEntity itemEntity = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).ownerId(2).build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemRequestStorage.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(List.of(itemRequestEntity));
        when(itemStorage.findByRequestId(1)).thenReturn(List.of(itemEntity));

        List<ItemResponseDto> actualItemResponseDtos = itemRequestService.getUsersItemRequests(userId);

        List<ItemResponseDto> expectedItemResponseDtos =
                ResourcePool.read(itemRequestService_sb_getUsersItemRequests_itemResponseDto, new TypeReference<>() {
                });

        assertEquals(expectedItemResponseDtos, actualItemResponseDtos);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemRequestStorage, Mockito.times(1)).findByRequestorIdOrderByCreatedDesc(userId);
        verify(itemStorage, Mockito.times(1)).findByRequestId(1);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemRequestStorage);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void getRequestById_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getRequestById(userId, 1));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoInteractions(itemRequestStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void getRequestById_shouldThrowExceptionWhenRequestNotFound() {
        Integer userId = 1;
        Integer requestId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.empty());

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getRequestById(userId, requestId));

        assertEquals("Item request with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemRequestStorage, Mockito.times(1)).findById(requestId);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void getRequestById_shouldReturnItemRequest() {
        Integer userId = 1;
        Integer requestId = 1;
        ItemRequestEntity itemRequestEntity = ItemRequestEntity.builder().id(1).description("Description")
                .requestorId(1).created(LocalDateTime.of(2023, 12, 8, 0, 0))
                .build();
        ItemEntity itemEntity1 = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).ownerId(2).build();
        ItemEntity itemEntity2 = ItemEntity.builder().id(2).name("Item 2").description("Item 2 description")
                .available(true).requestId(1).ownerId(2).build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.of(itemRequestEntity));
        when(itemStorage.findByRequestId(requestId)).thenReturn(List.of(itemEntity1, itemEntity2));

        ItemResponseDto actualItemResponseDto = itemRequestService.getRequestById(userId, requestId);

        ItemResponseDto expectedItemResponseDto = ResourcePool
                .read(itemRequestService_sb_getRequestById_itemResponseDto, ItemResponseDto.class);

        assertEquals(expectedItemResponseDto, actualItemResponseDto);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemRequestStorage, Mockito.times(1)).findById(requestId);
        verify(itemStorage, Mockito.times(1)).findByRequestId(requestId);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemRequestStorage);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void getAllRequests_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllRequests(userId, 0, 20));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoInteractions(itemRequestStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void getAllRequests_shouldReturnRequests() {
        Integer userId = 2;
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        ItemRequestEntity itemRequestEntity = ItemRequestEntity.builder().id(1).description("Description")
                .requestorId(1).created(LocalDateTime.of(2023, 12, 8, 0, 0))
                .build();
        ItemEntity itemEntity = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(1).ownerId(2).build();

        when(itemRequestStorage.findByRequestorIdNot(eq(userId), any())).thenReturn(List.of(itemRequestEntity));
        when(itemStorage.findByRequestId(1)).thenReturn(List.of(itemEntity));

        List<ItemResponseDto> actualItemResponseDtos = itemRequestService.getAllRequests(userId, 0, 20);

        List<ItemResponseDto> expectedItemResponseDtos =
                ResourcePool.read(itemRequestService_sb_getUsersItemRequests_itemResponseDto, new TypeReference<>() {
                });

        assertEquals(expectedItemResponseDtos, actualItemResponseDtos);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemRequestStorage, Mockito.times(1)).findByRequestorIdNot(eq(userId), any());
        verify(itemStorage, Mockito.times(1)).findByRequestId(1);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemRequestStorage);
        verifyNoMoreInteractions(itemStorage);

    }
}
