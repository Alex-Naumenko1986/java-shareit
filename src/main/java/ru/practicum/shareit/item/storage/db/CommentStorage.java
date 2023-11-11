package ru.practicum.shareit.item.storage.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.CommentEntity;

import java.util.List;

@Repository
public interface CommentStorage extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByItem_Id(Integer id);

    List<CommentEntity> findByItem_OwnerId(Integer ownerId);

}
