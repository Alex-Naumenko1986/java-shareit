package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemEntity;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemEntity toEntity(ItemDto itemDto);

    ItemDto toDto(ItemEntity itemEntity);
}
