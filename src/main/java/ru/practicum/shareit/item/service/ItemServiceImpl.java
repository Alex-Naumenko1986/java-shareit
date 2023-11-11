package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.mapper.BookingInfoMapper;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.IllegalAddCommentOperationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.storage.db.CommentStorage;
import ru.practicum.shareit.item.storage.db.ItemStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.db.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingInfoMapper bookingInfoMapper;

    @Override
    @Transactional
    public ItemDto createItem(int ownerId, ItemDto itemDto) {
        ItemEntity itemEntity = itemMapper.toEntity(itemDto);
        itemEntity.setOwnerId(ownerId);
        userStorage.findById(itemEntity.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , ownerId)));
        itemEntity = itemStorage.save(itemEntity);
        log.info("New item was added to database: {}", itemEntity);
        return itemMapper.toDto(itemEntity);
    }

    @Override
    @Transactional
    public ItemDto updateItem(int userId, ItemDto itemDto) {
        ItemEntity itemEntityUpdated = itemMapper.toEntity(itemDto);
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , userId)));
        ItemEntity itemEntity = itemStorage.findById(itemEntityUpdated.getId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d was not found"
                        , itemEntityUpdated.getId())));
        if (itemEntity.getOwnerId() != userId) {
            throw new UnsupportedOperationException(String.format("User with id: %d is not owner of the item " +
                    "with id: %d", userId, itemEntity.getId()));
        }
        updateItemFields(itemEntity, itemEntityUpdated);
        itemEntity = itemStorage.save(itemEntity);
        log.info("Item was updated in database: {}", itemEntity);
        return itemMapper.toDto(itemEntity);
    }

    @Override
    @Transactional
    public ItemDto getItemById(int userId, int itemId) {
        ItemEntity itemEntity = itemStorage.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d was not found", itemId)));
        userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , userId)));
        ItemDto itemDto = itemMapper.toDto(itemEntity);
        if (itemEntity.getOwnerId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            List<BookingEntity> bookings = bookingStorage.findByItem_IdAndStatusNotIn
                    (itemId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED));

            bookings.stream().filter(bookingEntity ->
                            bookingEntity.getStart().isBefore(now)).max(Comparator.comparing(BookingEntity::getEnd))
                    .ifPresent(lastBooking -> itemDto.setLastBooking(bookingInfoMapper.toDto(lastBooking)));

            bookings.stream().filter(bookingEntity ->
                            bookingEntity.getStart().isAfter(now)).min(Comparator.comparing(BookingEntity::getStart))
                    .ifPresent(nextBooking -> itemDto.setNextBooking(bookingInfoMapper.toDto(nextBooking)));
        }
        List<CommentEntity> comments = commentStorage.findByItem_Id(itemId);

        List<CommentDto> commentDtos = comments.stream().map(commentMapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);

        return itemDto;
    }

    @Override
    @Transactional
    public List<ItemDto> getOwnersItems(int ownerId) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , ownerId)));
        List<ItemEntity> itemEntities = itemStorage.findByOwnerId(ownerId);

        List<ItemDto> itemDtos = itemEntities.stream().map(itemMapper::toDto).collect(Collectors.toList());
        List<BookingEntity> bookings = bookingStorage.findByItem_OwnerId(ownerId);
        List<CommentEntity> comments = commentStorage.findByItem_OwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();
        for (ItemDto itemDto : itemDtos) {
            List<BookingEntity> itemBookings = bookings.stream()
                    .filter(bookingEntity -> Objects.equals(bookingEntity.getItem().getId(), itemDto.getId())
                            && bookingEntity.getStatus() != BookingStatus.REJECTED)
                    .collect(Collectors.toList());

            itemBookings.stream().filter(bookingEntity ->
                            bookingEntity.getStart().isBefore(now)).max(Comparator.comparing(BookingEntity::getEnd))
                    .ifPresent(lastBooking -> itemDto.setLastBooking(bookingInfoMapper.toDto(lastBooking)));

            itemBookings.stream().filter(bookingEntity ->
                            bookingEntity.getStart().isAfter(now)).min(Comparator.comparing(BookingEntity::getStart))
                    .ifPresent(nextBooking -> itemDto.setNextBooking(bookingInfoMapper.toDto(nextBooking)));

            List<CommentDto> itemComments = comments.stream()
                    .filter(commentEntity -> Objects.equals(commentEntity.getItem().getId(), itemDto.getId()))
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());

            itemDto.setComments(itemComments);
        }
        return itemDtos.stream().sorted(Comparator.comparing(ItemDto::getId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> searchItems(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemEntity> itemEntities = itemStorage.searchItems(query);
        return itemEntities.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        ItemEntity itemEntity = itemStorage.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d was not found", itemId)));
        UserEntity userEntity = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d was not found"
                        , userId)));

        List<BookingEntity> bookings = bookingStorage.findByItem_IdAndBooker_IdAndStatusAndEndBefore
                (itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new IllegalAddCommentOperationException(String.format("User with id: %d can't add comments to " +
                    "item with id: %d. This user didn't book this item", userId, itemId));
        }
        CommentEntity commentEntity = commentMapper.toEntity(commentDto);
        commentEntity.setAuthor(userEntity);
        commentEntity.setItem(itemEntity);
        commentEntity.setCreated(LocalDateTime.now());
        commentEntity = commentStorage.save(commentEntity);
        log.info("New comment was added to database: {}", commentEntity);
        return commentMapper.toDto(commentEntity);
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
