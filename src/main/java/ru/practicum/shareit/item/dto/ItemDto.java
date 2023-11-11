package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private List<CommentDto> comments;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private Integer itemRequestId;
}
