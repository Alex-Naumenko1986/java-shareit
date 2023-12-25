package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserService userService;
    private final EntityManager em;
    private final UserMapper userMapper;

    UserEntity userEntity1;
    UserEntity userEntity2;
    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        userEntity1 = UserEntity.builder().name("User 1").email("user1@yandex.ru").build();
        userEntity2 = UserEntity.builder().name("User 2").email("user2@yandex.ru").build();
        userDto1 = UserDto.builder().name("User 1").email("user1@yandex.ru").build();
        userDto2 = UserDto.builder().name("User 2").email("user2@yandex.ru").build();
    }


    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<UserDto> sourceUsers = List.of(userDto1, userDto2);
        for (UserDto user : sourceUsers) {
            em.persist(userMapper.toEntity(user));
        }

        em.flush();

        List<UserDto> actualUsers = userService.getAllUsers();
        for (UserDto user : sourceUsers) {
            assertThat(actualUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @Test
    void createUser_shouldCreateUser() {
        userService.createUser(userDto1);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.email = :email",
                UserEntity.class);
        UserEntity user = query.setParameter("email", "user1@yandex.ru").getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        em.persist(userEntity1);
        em.flush();
        Integer id = userEntity1.getId();

        UserDto updatedUser = UserDto.builder().id(id).email("user1_updated@yandex.ru").build();
        UserDto actualUser = userService.updateUser(updatedUser);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.email = :email",
                UserEntity.class);
        UserEntity userFromDb = query.setParameter("email", "user1_updated@yandex.ru").getSingleResult();

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo("User 1"));
        assertThat(actualUser.getEmail(), equalTo("user1_updated@yandex.ru"));
        assertThat(userFromDb.getId(), notNullValue());
        assertThat(userFromDb.getName(), equalTo("User 1"));
        assertThat(userFromDb.getEmail(), equalTo("user1_updated@yandex.ru"));
    }

    @Test
    void getUserById_shouldReturnUser() {
        em.persist(userEntity1);
        em.flush();
        Integer id = userEntity1.getId();

        UserDto actualUser = userService.getUserById(id);

        assertThat(actualUser.getId(), notNullValue());
        assertThat(actualUser.getName(), equalTo("User 1"));
        assertThat(actualUser.getEmail(), equalTo("user1@yandex.ru"));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        em.persist(userEntity1);
        em.flush();
        Integer id = userEntity1.getId();

        userService.deleteUser(id);

        TypedQuery<UserEntity> query = em.createQuery("Select u from UserEntity u where u.id = :id",
                UserEntity.class);
        assertThrows(NoResultException.class, () -> query.setParameter("id", id).getSingleResult());

    }


}
