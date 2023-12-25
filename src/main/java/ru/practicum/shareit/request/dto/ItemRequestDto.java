package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    @NotBlank(message = "Field description should not be blank")
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
}
