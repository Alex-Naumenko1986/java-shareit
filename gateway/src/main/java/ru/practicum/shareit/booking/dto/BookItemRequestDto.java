package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookItemRequestDto {
    private int itemId;
    @NotNull(message = "Field start should not be null")
    @FutureOrPresent(message = "Field start should be in future or in present")
    private LocalDateTime start;
    @NotNull(message = "Field end should not be null")
    @Future(message = "Field end should be in future")
    private LocalDateTime end;
}
