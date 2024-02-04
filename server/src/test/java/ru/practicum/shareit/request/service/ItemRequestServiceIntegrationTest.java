package ru.practicum.shareit.request.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.model.UserEntity;
import utils.ResourcePool;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static utils.ResourcePool.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final EntityManager em;

    @Test
    void createItemRequest_shouldCreateItemRequest() {
        UserEntity userEntity = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        em.persist(userEntity);
        em.flush();
        Integer userId = userEntity.getId();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Description").build();

        ItemRequestDto actualItemRequestDto = itemRequestService.createItemRequest(userId, itemRequestDto);

        assertNotNull(actualItemRequestDto.getId());
        assertEquals("Description", actualItemRequestDto.getDescription());
        assertEquals(userId, actualItemRequestDto.getRequestorId());
        assertFalse(actualItemRequestDto.getCreated().isAfter(LocalDateTime.now()));
    }

    @Test
    void getUsersItemRequests_shouldReturnItemRequests() {
        UserEntity userEntity1 = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity userEntity2 = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(userEntity1);
        em.persist(userEntity2);
        em.flush();
        Integer user1Id = userEntity1.getId();
        Integer user2Id = userEntity2.getId();

        List<ItemRequestEntity> itemRequestEntities =
                ResourcePool.read(itemRequestService_it_getUsersItemRequests_itemRequestEntity,
                        new TypeReference<>() {
                        });
        for (ItemRequestEntity itemRequestEntity : itemRequestEntities) {
            itemRequestEntity.setRequestorId(user2Id);
            em.persist(itemRequestEntity);
        }
        em.flush();

        ItemEntity itemEntity1 = ItemEntity.builder().name("Item 1").description("Item 1 description").available(true)
                .ownerId(user1Id).requestId(itemRequestEntities.get(0).getId()).build();
        ItemEntity itemEntity2 = ItemEntity.builder().name("Item 2").description("Item 2 description").available(true)
                .ownerId(user1Id).requestId(itemRequestEntities.get(1).getId()).build();
        em.persist(itemEntity1);
        em.persist(itemEntity2);
        em.flush();

        List<ItemResponseDto> actualItemResponseDtos = itemRequestService.getUsersItemRequests(user2Id);
        List<ItemResponseDto> expectedItemResponseDtos =
                ResourcePool.read(itemRequestService_it_getUsersItemRequests_itemResponseDto, new TypeReference<>() {
                });

        for (int i = 0; i < actualItemResponseDtos.size(); i++) {
            ItemResponseDto expectedItemResponseDto = expectedItemResponseDtos.get(i);
            ItemResponseDto actualItemResponseDto = actualItemResponseDtos.get(i);

            assertThat(expectedItemResponseDto)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "items")
                    .isEqualTo(actualItemResponseDto);
            assertThat(expectedItemResponseDto.getItems())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "requestId")
                    .isEqualTo(actualItemResponseDto.getItems());
            assertNotNull(actualItemResponseDto.getId());
            assertNotNull(actualItemResponseDto.getItems().get(0).getId());
            assertNotNull(actualItemResponseDto.getItems().get(0).getRequestId());
        }
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        UserEntity userEntity1 = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity userEntity2 = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(userEntity1);
        em.persist(userEntity2);
        em.flush();
        Integer user1Id = userEntity1.getId();
        Integer user2Id = userEntity2.getId();

        ItemRequestEntity itemRequestEntity = ItemRequestEntity.builder().description("Description")
                .created(LocalDateTime.of(2023, 12, 10, 0, 0)).requestorId(user2Id)
                .build();
        em.persist(itemRequestEntity);
        em.flush();
        Integer requestId = itemRequestEntity.getId();

        ItemEntity itemEntity1 = ItemEntity.builder().name("Item 1").description("Item 1 description").available(true)
                .ownerId(user1Id).requestId(itemRequestEntity.getId()).build();
        em.persist(itemEntity1);
        em.flush();

        ItemResponseDto actualItemResponseDto = itemRequestService.getRequestById(user2Id, requestId);
        ItemResponseDto expectedItemResponseDto = ResourcePool
                .read(itemRequestService_it_getRequestById_itemResponseDto, ItemResponseDto.class);

        assertThat(expectedItemResponseDto)
                .usingRecursiveComparison()
                .ignoringFields("id", "items")
                .isEqualTo(actualItemResponseDto);
        assertThat(expectedItemResponseDto.getItems())
                .usingRecursiveComparison()
                .ignoringFields("id", "requestId")
                .isEqualTo(actualItemResponseDto.getItems());
        assertNotNull(actualItemResponseDto.getId());
        assertNotNull(actualItemResponseDto.getItems().get(0).getId());
        assertNotNull(actualItemResponseDto.getItems().get(0).getRequestId());
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() {
        UserEntity userEntity1 = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity userEntity2 = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        em.persist(userEntity1);
        em.persist(userEntity2);
        em.flush();
        Integer user1Id = userEntity1.getId();
        Integer user2Id = userEntity2.getId();

        List<ItemRequestEntity> itemRequestEntities =
                ResourcePool.read(itemRequestService_it_getUsersItemRequests_itemRequestEntity,
                        new TypeReference<>() {
                        });
        for (ItemRequestEntity itemRequestEntity : itemRequestEntities) {
            itemRequestEntity.setRequestorId(user2Id);
            em.persist(itemRequestEntity);
        }
        em.flush();

        ItemEntity itemEntity1 = ItemEntity.builder().name("Item 1").description("Item 1 description").available(true)
                .ownerId(user1Id).requestId(itemRequestEntities.get(0).getId()).build();
        ItemEntity itemEntity2 = ItemEntity.builder().name("Item 2").description("Item 2 description").available(true)
                .ownerId(user1Id).requestId(itemRequestEntities.get(1).getId()).build();
        em.persist(itemEntity1);
        em.persist(itemEntity2);
        em.flush();

        List<ItemResponseDto> actualItemResponseDtos = itemRequestService.getAllRequests(user1Id, 0, 20);
        List<ItemResponseDto> expectedItemResponseDtos =
                ResourcePool.read(itemRequestService_it_getUsersItemRequests_itemResponseDto, new TypeReference<>() {
                });

        for (int i = 0; i < actualItemResponseDtos.size(); i++) {
            ItemResponseDto expectedItemResponseDto = expectedItemResponseDtos.get(i);
            ItemResponseDto actualItemResponseDto = actualItemResponseDtos.get(i);

            assertThat(expectedItemResponseDto)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "items")
                    .isEqualTo(actualItemResponseDto);
            assertThat(expectedItemResponseDto.getItems())
                    .usingRecursiveComparison()
                    .ignoringFields("id", "requestId")
                    .isEqualTo(actualItemResponseDto.getItems());
            assertNotNull(actualItemResponseDto.getId());
            assertNotNull(actualItemResponseDto.getItems().get(0).getId());
            assertNotNull(actualItemResponseDto.getItems().get(0).getRequestId());
        }
    }
}
