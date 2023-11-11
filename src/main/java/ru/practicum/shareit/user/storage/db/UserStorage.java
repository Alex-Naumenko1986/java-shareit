package ru.practicum.shareit.user.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.UserEntity;

public interface UserStorage extends JpaRepository<UserEntity, Integer> {

}
