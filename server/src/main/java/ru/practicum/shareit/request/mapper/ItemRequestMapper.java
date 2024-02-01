package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestEntity;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestEntity toEntity(ItemRequestDto itemRequestDto);

    ItemRequestDto toDto(ItemRequestEntity itemRequestEntity);
}


