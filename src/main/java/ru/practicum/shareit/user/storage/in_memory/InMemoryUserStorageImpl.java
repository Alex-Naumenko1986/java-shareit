package ru.practicum.shareit.user.storage.in_memory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.id_generator.IdGenerator;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.*;

@Component
@AllArgsConstructor
@Slf4j
public class InMemoryUserStorageImpl implements InMemoryUserStorage {

    private final IdGenerator idGenerator;
    private final Map<Integer, UserEntity> idToUser = new HashMap<>();

    @Override
    public List<UserEntity> getAllUsers() {
        return new ArrayList<>(idToUser.values());
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        checkEmailAlreadyExists(userEntity);
        int id = idGenerator.generateId();
        userEntity.setId(id);
        idToUser.put(id, userEntity);
        log.info("New user with id: {} was created: {}", userEntity.getId(), userEntity);
        return userEntity;
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        checkEmailAlreadyExists(userEntity);
        if (!idToUser.containsKey(userEntity.getId())) {
            throw new UserNotFoundException(String.format("User with id: %d does not exist", userEntity.getId()));
        }
        UserEntity userEntityFromStorage = idToUser.get(userEntity.getId());
        updateUserFields(userEntityFromStorage, userEntity);
        idToUser.replace(userEntityFromStorage.getId(), userEntityFromStorage);
        log.info("User with id: {} was updated: {}", userEntityFromStorage.getId(), userEntityFromStorage);
        return userEntityFromStorage;
    }

    @Override
    public UserEntity getUserById(int id) {
        if (!idToUser.containsKey(id)) {
            throw new UserNotFoundException(String.format("User with id: %d does not exist", id));
        }
        return idToUser.get(id);
    }

    @Override
    public void deleteUser(int id) {
        if (!idToUser.containsKey(id)) {
            throw new UserNotFoundException(String.format("User with id: %d does not exist", id));
        }
        idToUser.remove(id);
        log.info("User with id: {} was removed from storage", id);
    }

    private void checkEmailAlreadyExists(UserEntity userEntity) {
        Optional<UserEntity> userWithSameEmail = idToUser.values().stream()
                .filter(user1 -> user1.getEmail().equals(userEntity.getEmail())
                        && !Objects.equals(user1.getId(), userEntity.getId()))
                .findAny();
        if (userWithSameEmail.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with e-mail: %s already exists", userEntity.getEmail()));
        }
    }

    private void updateUserFields(UserEntity userEntityFromStorage, UserEntity userEntity) {

        if (userEntity.getName() != null && !userEntity.getName().isBlank()) {
            userEntityFromStorage.setName(userEntity.getName());
        }

        if (userEntity.getEmail() != null && !userEntity.getEmail().isBlank()) {
            userEntityFromStorage.setEmail(userEntity.getEmail());
        }
    }
}
