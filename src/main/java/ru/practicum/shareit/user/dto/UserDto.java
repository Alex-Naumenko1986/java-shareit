package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Integer id;
    @NotBlank(message = "Field name should not be blank")
    private String name;
    @NotBlank(message = "Field email should not be blank")
    @Email(message = "Invalid email")
    private String email;
}
