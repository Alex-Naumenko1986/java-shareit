package ru.practicum.shareit.item.storage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemStorageTest {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserStorage userStorage;

    @Test
    @Transactional
    void findByOwnerId_shouldReturnItem() {
        UserEntity userEntity1 = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        UserEntity userEntity2 = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        userEntity1 = userStorage.save(userEntity1);
        userEntity2 = userStorage.save(userEntity2);
        Integer user1Id = userEntity1.getId();
        Integer user2Id = userEntity2.getId();

        ItemEntity itemEntity1 = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(user1Id).build();
        ItemEntity itemEntity2 = ItemEntity.builder().name("Item 2").description("Item 2 description")
                .available(true).ownerId(user2Id).build();
        itemStorage.save(itemEntity1);
        itemStorage.save(itemEntity2);

        Pageable pageable = PageRequest.of(0, 20);

        List<ItemEntity> actualItemEntities = itemStorage.findByOwnerId(user1Id, pageable);

        assertEquals(1, actualItemEntities.size());

        Assertions.assertThat(itemEntity1)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actualItemEntities.get(0));
        assertNotNull(actualItemEntities.get(0).getId());
    }

    @Test
    @Transactional
    void searchItems_shouldReturnItem() {
        UserEntity userEntity1 = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        userEntity1 = userStorage.save(userEntity1);
        Integer user1Id = userEntity1.getId();

        ItemEntity itemEntity1 = ItemEntity.builder().name("Item 1").description("Item 1 description")
                .available(true).ownerId(user1Id).build();
        ItemEntity itemEntity2 = ItemEntity.builder().name("Item 2").description("Electric drill")
                .available(true).ownerId(user1Id).build();
        itemStorage.save(itemEntity1);
        itemStorage.save(itemEntity2);

        Pageable pageable = PageRequest.of(0, 20);

        List<ItemEntity> actualItemEntities = itemStorage.searchItems("Drill", pageable);

        assertEquals(1, actualItemEntities.size());

        Assertions.assertThat(itemEntity2)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(actualItemEntities.get(0));
        assertNotNull(actualItemEntities.get(0).getId());
    }
}
