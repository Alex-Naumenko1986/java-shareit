package ru.practicum.shareit.user.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.UserEntity;

@Repository
public interface UserStorage extends JpaRepository<UserEntity, Integer> {

}
