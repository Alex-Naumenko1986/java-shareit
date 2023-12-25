package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.ItemEntity;

@Mapper(componentModel = "spring")
public interface ShortItemMapper {
    ShortItemDto toDto(ItemEntity itemEntity);
}
