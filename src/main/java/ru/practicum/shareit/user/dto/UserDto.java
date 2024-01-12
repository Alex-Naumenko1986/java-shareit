package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    @NotBlank(message = "Field name should not be blank")
    private String name;
    @NotBlank(message = "Field email should not be blank")
    @Email(message = "Invalid email")
    private String email;
}
