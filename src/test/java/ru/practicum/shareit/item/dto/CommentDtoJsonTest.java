package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import utils.ResourcePool;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.ResourcePool.commentDto;


@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void commentDto_shouldDeserialize() {
        CommentDto expectedCommentDto = CommentDto.builder().id(1).text("Comment 1").authorName("User 1")
                .created(LocalDateTime.of(2023, 12, 1, 10, 30)).build();

        CommentDto actualCommentDto = json.readObject(commentDto);

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @SneakyThrows
    @Test
    void commentDto_shouldSerialize() {
        CommentDto commentDto = CommentDto.builder().id(1).text("Comment 1").authorName("User 1")
                .created(LocalDateTime.of(2023, 12, 1, 10, 30)).build();

        JsonContent<CommentDto> commentDtoAsJson = json.write(commentDto);

        assertThat(commentDtoAsJson).isEqualToJson(ResourcePool.commentDto);
    }
}
