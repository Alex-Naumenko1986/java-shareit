package ru.practicum.shareit.user.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.id_generator.IdGenerator;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@AllArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final IdGenerator idGenerator;
    private final Map<Integer, User> idToUser = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(idToUser.values());
    }

    @Override
    public User createUser(User user) {
        if (idToUser.containsKey(user.getId())) {
            throw new UserAlreadyExistsException(String.format("User with id: %d already exists", user.getId()));
        }
        checkEmailAlreadyExists(user);
        int id = idGenerator.generateId();
        user.setId(id);
        idToUser.put(id, user);
        log.info("New user was created: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!idToUser.containsKey(user.getId())) {
            throw new UserNotFoundException(String.format("User with id: %d does not exist", user.getId()));
        }
        User userFromStorage = idToUser.get(user.getId());
        checkEmailAlreadyExists(user);
        updateUserFields(userFromStorage, user);
        idToUser.replace(userFromStorage.getId(), userFromStorage);
        log.info("User was updated: {}", userFromStorage);
        return userFromStorage;
    }

    @Override
    public User getUserById(int id) {
        if (!idToUser.containsKey(id)) {
            throw new UserNotFoundException(String.format("User with id: %d does not exist", id));
        }
        return idToUser.get(id);
    }

    @Override
    public void deleteUser(int id) {
        idToUser.remove(id);
    }

    private void checkEmailAlreadyExists(User user) {
        Optional<User> userWithSameEmail = idToUser.values().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail())
                        && !Objects.equals(user1.getId(), user.getId()))
                .findAny();
        if (userWithSameEmail.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with e-mail: %s already exists", user.getEmail()));
        }
    }

    private void updateUserFields(User userFromStorage, User user) {

        if (user.getName() != null && !user.getName().isBlank()) {
            userFromStorage.setName(user.getName());
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userFromStorage.setEmail(user.getEmail());
        }
    }
}
