package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequestEntity;

@Mapper(componentModel = "spring")
public interface ItemResponseMapper {
    ItemResponseDto toDto(ItemRequestEntity itemRequestEntity);
}
