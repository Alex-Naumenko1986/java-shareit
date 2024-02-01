package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {
    @NotBlank(message = "Field name should not be blank")
    private String name;
    @NotBlank(message = "Field description should not be blank")
    private String description;
    @NotNull(message = "Field available should not be null")
    private Boolean available;
    private Integer requestId;
}
