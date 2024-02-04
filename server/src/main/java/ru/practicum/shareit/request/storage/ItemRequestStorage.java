package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequestEntity;

import java.util.List;

@Repository
public interface ItemRequestStorage extends JpaRepository<ItemRequestEntity, Integer> {
    List<ItemRequestEntity> findByRequestorIdNot(Integer requestorId, Pageable pageable);

    List<ItemRequestEntity> findByRequestorIdOrderByCreatedDesc(Integer requestorId);
}
