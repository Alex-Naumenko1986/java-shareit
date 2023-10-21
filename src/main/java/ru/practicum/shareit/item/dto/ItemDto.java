package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Field name should not be blank")
    private String name;
    @NotBlank(message = "Field description should not be blank")
    private String description;
    @NotNull(message = "Field available should not be null")
    private Boolean available;
    private Integer itemRequestId;
}
