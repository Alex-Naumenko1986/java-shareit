package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.mapper.BookingInfoMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.IllegalAddCommentOperationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.UserStorage;
import utils.ResourcePool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static utils.ResourcePool.*;

@SpringBootTest(classes = {ItemServiceImpl.class, ItemMapperImpl.class, CommentMapper.class, BookingInfoMapper.class})
public class ItemServiceSpringBootTest {
    @Autowired
    private ItemServiceImpl itemService;
    @MockBean
    private UserStorage userStorage;
    @MockBean
    private ItemStorage itemStorage;
    @MockBean
    private ItemRequestStorage itemRequestStorage;
    @MockBean
    private CommentStorage commentStorage;
    @MockBean
    private BookingStorage bookingStorage;

    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder().name("Item 1").description("Item 1 description").available(true).build();
    }

    @Test
    void createItem_shouldThrowExceptionWhenItemOwnerNotFound() {
        Integer ownerId = 1;
        when(userStorage.findById(ownerId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(ownerId, itemDto));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(ownerId);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(itemRequestStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createItem_shouldThrowExceptionWhenItemRequestNotFound() {
        Integer ownerId = 1;
        Integer requestId = 1;
        itemDto.setRequestId(requestId);
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(new UserEntity()));
        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.empty());

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemService.createItem(ownerId, itemDto));

        assertEquals("Item request with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(ownerId);
        verify(itemRequestStorage, Mockito.times(1)).findById(requestId);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemRequestStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void createItem_shouldCreateItem() {
        Integer ownerId = 1;
        Integer requestId = 1;
        itemDto.setRequestId(requestId);
        ItemEntity savedItemEntity = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(requestId).build();
        ItemDto expectedItemDto = ItemDto.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).requestId(requestId).build();
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(new UserEntity()));
        when(itemRequestStorage.findById(requestId)).thenReturn(Optional.of(new ItemRequestEntity()));
        when(itemStorage.save(any())).thenReturn(savedItemEntity);

        ItemDto createdItemDto = itemService.createItem(ownerId, itemDto);

        assertEquals(expectedItemDto, createdItemDto);
        verify(userStorage, Mockito.times(1)).findById(ownerId);
        verify(itemRequestStorage, Mockito.times(1)).findById(requestId);
        verify(itemStorage, Mockito.times(1)).save(any());
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemRequestStorage);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(userId, itemDto));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenItemNotFound() {
        Integer userId = 1;
        Integer itemId = 1;
        itemDto.setId(itemId);
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(userId, itemDto));

        assertEquals("Item with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenNotOwnerUpdatingItem() {
        Integer itemId = 1;
        Integer userId = 1;
        itemDto.setId(itemId);

        ItemEntity itemEntity = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).ownerId(2).build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(itemEntity));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> itemService.updateItem(userId, itemDto));

        assertEquals("User with id: 1 is not owner of the item with id: 1", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void updateUser_shouldUpdateUser() {
        Integer userId = 2;
        Integer itemId = 1;
        itemDto.setId(1);
        ItemEntity itemEntity = ItemEntity.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).ownerId(2).build();
        ItemEntity itemEntityUpdated = ItemEntity.builder().id(1).name("Item 1 updated")
                .description("Item 1 description updated")
                .available(true).ownerId(2).build();
        ItemDto itemDtoUpdated = ItemDto.builder().id(itemId).name("Item 1 updated")
                .description("Item 1 description updated").available(true).build();
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(itemEntity));
        when(itemStorage.save(any())).thenReturn(itemEntityUpdated);

        ItemDto actualItemDto = itemService.updateItem(userId, itemDtoUpdated);

        assertEquals(itemDtoUpdated, actualItemDto);
        Mockito.verify(itemStorage, Mockito.times(1)).save(any());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void getItemById_shouldThrowExceptionWhenItemNotFound() {
        Integer itemId = 1;
        Integer userId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(userId, itemId));

        assertEquals("Item with id 1 was not found", exception.getMessage());
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verifyNoMoreInteractions(itemStorage);
        verifyNoInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(commentStorage);
    }

    @Test
    void getItemById_shouldThrowExceptionWhenUserNotFound() {
        Integer itemId = 1;
        Integer userId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(ItemEntity.builder().build()));
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.getItemById(userId, itemId));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(commentStorage);
    }

    @Test
    void getItemById_shouldReturnItemWithBookingInfoWhenRequestedByOwner() {
        Integer itemId = 1;
        Integer userId = 1;
        ItemEntity itemEntity = ResourcePool.read(getItemById_itemEntity, ItemEntity.class);
        List<BookingEntity> bookingEntities = ResourcePool.read(getItemById_bookingEntities, new TypeReference<>() {
        });

        List<CommentEntity> commentEntities = ResourcePool.read(getItemById_commentEntities, new TypeReference<>() {
        });

        ItemDto expectedItemDto = ResourcePool.read(getItemById_itemDto, ItemDto.class);

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(itemEntity));
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bookingStorage.findByItem_IdAndStatusNotIn(itemId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED)))
                .thenReturn(bookingEntities);
        when(commentStorage.findByItem_Id(itemId)).thenReturn(commentEntities);

        ItemDto actualItemDto = itemService.getItemById(userId, itemId);

        assertEquals(expectedItemDto, actualItemDto);
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(bookingStorage, times(1))
                .findByItem_IdAndStatusNotIn(itemId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED));
        verify(commentStorage, times(1)).findByItem_Id(itemId);
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(bookingStorage);
        verifyNoMoreInteractions(commentStorage);
    }

    @Test
    void getOwnersItems_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.getOwnersItems(userId, 0, 20));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(itemStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(commentStorage);
    }


    @Test
    void getOwnersItems_shouldReturnItemsWithBookingInfoAndComments() {
        Integer ownerId = 1;
        List<ItemEntity> itemEntities = ResourcePool.read(getOwnersItems_ItemEntities, new TypeReference<>() {
        });
        List<BookingEntity> bookingEntities = ResourcePool.read(getOwnersItems_bookingEntities, new TypeReference<>() {
        });
        List<CommentEntity> commentEntities = ResourcePool.read(getOwnersItems_commentEntities,
                new TypeReference<>() {
                });

        when(userStorage.findById(ownerId)).thenReturn(Optional.of(new UserEntity()));
        when(itemStorage.findByOwnerId(eq(ownerId), any())).thenReturn(itemEntities);
        when(bookingStorage.findByItem_OwnerId(ownerId)).thenReturn(bookingEntities);
        when(commentStorage.findByItem_OwnerId(ownerId)).thenReturn(commentEntities);

        List<ItemDto> actualItemDtos = itemService.getOwnersItems(ownerId, 0, 20);
        List<ItemDto> expectedItemDtos = ResourcePool.read(getOwnersItems_itemDtos, new TypeReference<>() {
        });

        assertEquals(expectedItemDtos, actualItemDtos);
        verify(userStorage, times(1)).findById(ownerId);
        verify(itemStorage, times(1)).findByOwnerId(eq(ownerId), any());
        verify(bookingStorage, times(1)).findByItem_OwnerId(ownerId);
        verify(commentStorage, times(1)).findByItem_OwnerId(ownerId);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(bookingStorage);
        verifyNoMoreInteractions(commentStorage);
    }

    @Test
    void searchItems_shouldReturnEmptyListWhenQueryIsBlank() {
        List<ItemDto> actualItemDtos = itemService.searchItems(" ", 0, 20);
        List<ItemDto> expectedItemDtos = new ArrayList<>();

        assertEquals(expectedItemDtos, actualItemDtos);
        verifyNoInteractions(itemStorage);
    }

    @Test
    void searchItems_shouldReturnItemList() {
        List<ItemEntity> itemEntities = ResourcePool.read(searchItems_itemEntity, new TypeReference<>() {
        });
        when(itemStorage.searchItems(eq("Item"), any())).thenReturn(itemEntities);

        List<ItemDto> actualItemDtos = itemService.searchItems("Item", 0, 20);

        List<ItemDto> expectedItemDtos = ResourcePool.read(searchItems_itemDto, new TypeReference<>() {
        });

        assertEquals(expectedItemDtos, actualItemDtos);
        verify(itemStorage, times(1)).searchItems(eq("Item"), any());
        verifyNoMoreInteractions(itemStorage);
    }

    @Test
    void addComment_shouldThrowExceptionWhenItemNotFound() {
        Integer itemId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(1, itemId, new CommentDto()));

        assertEquals("Item with id 1 was not found", exception.getMessage());
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verifyNoMoreInteractions(itemStorage);
        verifyNoInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(commentStorage);
    }

    @Test
    void addComment_shouldThrowExceptionWhenUserNotFound() {
        Integer userId = 1;
        when(itemStorage.findById(1)).thenReturn(Optional.of(new ItemEntity()));
        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.addComment(userId, 1, new CommentDto()));

        assertEquals("User with id 1 was not found", exception.getMessage());
        verify(itemStorage, Mockito.times(1)).findById(1);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(userStorage);
        verifyNoInteractions(bookingStorage);
        verifyNoInteractions(commentStorage);
    }

    @Test
    void addComment_shouldThrowExceptionWhenUserDidNotBookItem() {
        Integer itemId = 1;
        Integer userId = 1;
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(new ItemEntity()));
        when(userStorage.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bookingStorage.findByItem_IdAndBooker_IdAndStatusAndEndBefore(eq(itemId), eq(userId),
                eq(BookingStatus.APPROVED), any())).thenReturn(new ArrayList<>());

        IllegalAddCommentOperationException exception = assertThrows(IllegalAddCommentOperationException.class,
                () -> itemService.addComment(userId, itemId, new CommentDto()));

        assertEquals("User with id: 1 can't add comments to item with id: 1. This user didn't book this item",
                exception.getMessage());
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_IdAndBooker_IdAndStatusAndEndBefore(eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any());
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(bookingStorage);
        verifyNoInteractions(commentStorage);
    }

    @Test
    void addComment_shouldAddNewComment() {
        Integer itemId = 1;
        Integer userId = 1;
        UserEntity userEntity = UserEntity.builder().id(1).name("User 1").email("user1@yandex.ru").build();
        ItemEntity itemEntity = ItemEntity.builder().id(1).ownerId(2).name("Item 1").description("Item 1 description")
                .available(true).build();

        when(userStorage.findById(userId)).thenReturn(Optional.of(userEntity));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(itemEntity));
        when(bookingStorage.findByItem_IdAndBooker_IdAndStatusAndEndBefore(eq(itemId), eq(userId),
                eq(BookingStatus.APPROVED), any())).thenReturn(List.of(new BookingEntity()));
        when(commentStorage.save(any())).thenAnswer(
                invocationOnMock -> {
                    CommentEntity commentEntity = invocationOnMock.getArgument(0, CommentEntity.class);
                    commentEntity.setId(1);
                    return commentEntity;
                }
        );

        CommentDto commentDto = CommentDto.builder().text("Comment 1").build();

        CommentDto actualCommentDto = itemService.addComment(userId, itemId, commentDto);

        assertEquals(1, actualCommentDto.getId());
        assertEquals("Comment 1", actualCommentDto.getText());
        assertEquals("User 1", actualCommentDto.getAuthorName());
        assertFalse(actualCommentDto.getCreated().isAfter(LocalDateTime.now()));
        verify(itemStorage, Mockito.times(1)).findById(itemId);
        verify(userStorage, Mockito.times(1)).findById(userId);
        verify(bookingStorage, Mockito.times(1))
                .findByItem_IdAndBooker_IdAndStatusAndEndBefore(eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any());
        verify(commentStorage, times(1)).save(any());
        verifyNoMoreInteractions(itemStorage);
        verifyNoMoreInteractions(userStorage);
        verifyNoMoreInteractions(bookingStorage);
        verifyNoMoreInteractions(commentStorage);
    }

}
