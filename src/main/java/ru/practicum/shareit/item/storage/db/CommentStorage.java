package ru.practicum.shareit.item.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.CommentEntity;

import java.util.List;

public interface CommentStorage extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByItem_Id(Integer id);

    List<CommentEntity> findByItem_OwnerId(Integer ownerId);

}
