package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CommentDto {
    private int id;
    @NotBlank(message = "Text of the comment should not be blank")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
