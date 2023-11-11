package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.item.model.CommentEntity;

@Component
public class CommentMapper {
    public CommentEntity toEntity(CommentDto commentDto) {
        return CommentEntity.builder().text(commentDto.getText()).build();
    }

    public CommentDto toDto(CommentEntity commentEntity) {
        return CommentDto.builder().id(commentEntity.getId()).text(commentEntity.getText())
                .authorName(commentEntity.getAuthor().getName()).created(commentEntity.getCreated()).build();
    }
}
