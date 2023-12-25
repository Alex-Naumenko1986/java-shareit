package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    @NotBlank(message = "Text of the comment should not be blank")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
