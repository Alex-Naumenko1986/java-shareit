package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.BookingEntity;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;
import utils.ResourcePool;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static utils.ResourcePool.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final EntityManager em;
    private final ItemMapper itemMapper;

    private ItemDto itemDto;
    private UserEntity userEntity;
    private ItemEntity itemEntity;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder().name("Item 1").description("Item 1 description").available(true)
                .build();
        itemEntity = ItemEntity.builder().name("Item 1").description("Item 1 description").available(true)
                .build();
        userEntity = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
    }

    @Test
    void createItem_shouldCreateItem() {
        em.persist(userEntity);
        em.flush();
        Integer userId = userEntity.getId();

        itemService.createItem(userId, itemDto);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.name = :name",
                ItemEntity.class);
        ItemEntity itemEntity = query.setParameter("name", "Item 1").getSingleResult();

        assertNotNull(itemEntity.getId());
        assertEquals(itemDto.getName(), itemEntity.getName());
        assertEquals(itemDto.getDescription(), itemEntity.getDescription());
        assertEquals(itemDto.getAvailable(), itemEntity.getAvailable());
    }

    @Test
    void updateItem_shouldUpdateItem() {
        em.persist(userEntity);
        em.flush();
        Integer userId = userEntity.getId();
        itemEntity.setOwnerId(userId);

        em.persist(itemEntity);
        Integer itemId = itemEntity.getId();

        ItemDto updatedItemDto = ItemDto.builder().id(itemId).name("Item 1 updated").description("Item 1 updated " +
                "description").available(false).build();

        ItemDto actualItemDto = itemService.updateItem(userId, updatedItemDto);

        TypedQuery<ItemEntity> query = em.createQuery("Select i from ItemEntity i where i.id = :id",
                ItemEntity.class);
        ItemEntity itemEntityFromDb = query.setParameter("id", itemId).getSingleResult();

        assertEquals(updatedItemDto, actualItemDto);
        assertNotNull(itemEntityFromDb.getId());
        assertEquals("Item 1 updated", itemEntityFromDb.getName());
        assertEquals("Item 1 updated description", itemEntityFromDb.getDescription());
        assertFalse(itemEntityFromDb.getAvailable());
    }

    @Test
    void getItemById_shouldReturnItem() {
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(userEntity);
        em.persist(booker);
        em.flush();
        Integer ownerId = userEntity.getId();
        itemEntity.setOwnerId(ownerId);
        em.persist(itemEntity);
        em.flush();
        Integer itemId = itemEntity.getId();

        List<BookingEntity> bookings = ResourcePool.read(it_getItemById_bookingEntity, new TypeReference<>() {
        });
        for (BookingEntity booking : bookings) {
            booking.setItem(itemEntity);
            booking.setBooker(booker);
            em.persist(booking);
        }
        em.flush();

        List<CommentEntity> comments = ResourcePool.read(it_getItemById_commentEntity, new TypeReference<>() {
        });
        for (CommentEntity comment : comments) {
            comment.setAuthor(booker);
            comment.setItem(itemEntity);
            em.persist(comment);
        }
        em.flush();

        ItemDto actualItemDto = itemService.getItemById(ownerId, itemId);

        ItemDto expectedItemDto = ResourcePool.read(getItemById_itemDto, ItemDto.class);

        Assertions.assertThat(expectedItemDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                .isEqualTo(actualItemDto);
        Assertions.assertThat(expectedItemDto.getComments())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actualItemDto.getComments());
        Assertions.assertThat(expectedItemDto.getLastBooking())
                .usingRecursiveComparison()
                .ignoringFields("id", "bookerId")
                .isEqualTo(actualItemDto.getLastBooking());
        Assertions.assertThat(expectedItemDto.getNextBooking())
                .usingRecursiveComparison()
                .ignoringFields("id", "bookerId")
                .isEqualTo(actualItemDto.getNextBooking());
        assertNotNull(actualItemDto.getId());
        assertNotNull(actualItemDto.getLastBooking().getId());
        assertNotNull(actualItemDto.getLastBooking().getBookerId());
        assertNotNull(actualItemDto.getNextBooking().getId());
        assertNotNull(actualItemDto.getNextBooking().getBookerId());
        assertNotNull(actualItemDto.getComments().get(0).getId());
        assertNotNull(actualItemDto.getComments().get(1).getId());
    }

    @Test
    void getOwnersItems_shouldReturnItems() {
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(userEntity);
        em.persist(booker);
        em.flush();
        Integer ownerId = userEntity.getId();
        itemEntity.setOwnerId(ownerId);
        ItemEntity otherItem = ItemEntity.builder().name("Item 2").description("Item 2 description").available(true)
                .build();
        otherItem.setOwnerId(ownerId);
        em.persist(itemEntity);
        em.persist(otherItem);
        em.flush();

        List<BookingEntity> bookings = ResourcePool.read(it_getOwnersItems_bookingEntity, new TypeReference<>() {
        });
        for (int i = 0; i < bookings.size(); i++) {
            BookingEntity booking = bookings.get(i);
            if (i < 2) {
                booking.setItem(itemEntity);
            } else {
                booking.setItem(otherItem);
            }
            booking.setBooker(booker);
            em.persist(booking);
        }
        em.flush();

        List<CommentEntity> comments = ResourcePool.read(it_getOwnersItems_commentEntity, new TypeReference<>() {
        });
        comments.get(0).setItem(itemEntity);
        comments.get(1).setItem(otherItem);
        for (CommentEntity comment : comments) {
            comment.setAuthor(booker);
            em.persist(comment);
        }
        em.flush();

        List<ItemDto> actualItemDtos = itemService.getOwnersItems(ownerId, 0, 10);

        List<ItemDto> expectedItemDtos = ResourcePool.read(getOwnersItems_itemDtos,
                new TypeReference<>() {
                });

        for (int i = 0; i < actualItemDtos.size(); i++) {
            ItemDto actualItemDto = actualItemDtos.get(i);
            ItemDto expectedItemDto = expectedItemDtos.get(i);
            Assertions.assertThat(expectedItemDto)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "comments", "nextBooking", "lastBooking")
                    .isEqualTo(actualItemDto);
            Assertions.assertThat(expectedItemDto.getComments())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(actualItemDto.getComments());
            Assertions.assertThat(expectedItemDto.getLastBooking())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "bookerId")
                    .isEqualTo(actualItemDto.getLastBooking());
            Assertions.assertThat(expectedItemDto.getNextBooking())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "bookerId")
                    .isEqualTo(actualItemDto.getNextBooking());
            assertNotNull(actualItemDto.getId());
            assertNotNull(actualItemDto.getLastBooking().getId());
            assertNotNull(actualItemDto.getLastBooking().getBookerId());
            assertNotNull(actualItemDto.getNextBooking().getId());
            assertNotNull(actualItemDto.getNextBooking().getBookerId());
            assertNotNull(actualItemDto.getComments().get(0).getId());
        }
    }

    @Test
    void searchItems_shouldReturnItem() {
        em.persist(userEntity);
        em.flush();
        Integer ownerId = userEntity.getId();
        ItemEntity otherItem = ItemEntity.builder().name("Item 2").description("Toothbrush").available(true)
                .build();
        itemEntity.setOwnerId(ownerId);
        otherItem.setOwnerId(ownerId);
        em.persist(itemEntity);
        em.persist(otherItem);
        em.flush();

        List<ItemDto> actualItemDtos = itemService.searchItems("brush", 0, 10);
        ItemDto expectedItemDto = ItemDto.builder().name("Item 2").description("Toothbrush")
                .available(true).build();

        assertEquals(1, actualItemDtos.size());
        Assertions.assertThat(expectedItemDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actualItemDtos.get(0));
        assertNotNull(actualItemDtos.get(0).getId());
    }

    @Test
    void addComment_shouldAddComment() {
        UserEntity booker = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(userEntity);
        em.persist(booker);
        em.flush();
        Integer ownerId = userEntity.getId();
        itemEntity.setOwnerId(ownerId);
        em.persist(itemEntity);
        em.flush();
        Integer itemId = itemEntity.getId();
        Integer bookerId = booker.getId();

        List<BookingEntity> bookings = ResourcePool.read(it_addComment_bookingEntity, new TypeReference<>() {
        });
        for (BookingEntity booking : bookings) {
            booking.setItem(itemEntity);
            booking.setBooker(booker);
            em.persist(booking);
        }
        em.flush();
        CommentDto commentDto = CommentDto.builder().text("Comment 1").build();

        CommentDto actualCommentDto = itemService.addComment(bookerId, itemId, commentDto);

        assertNotNull(actualCommentDto.getId());
        assertEquals("Comment 1", actualCommentDto.getText());
        assertEquals("User 2", actualCommentDto.getAuthorName());
        assertFalse(actualCommentDto.getCreated().isAfter(LocalDateTime.now()));
    }
}
