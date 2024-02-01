package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.validation.UserCreateValidation;
import ru.practicum.shareit.user.validation.UserUpdateValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    @NotBlank(groups = {UserCreateValidation.class}, message = "Field name can not be blank")
    private String name;
    @NotBlank(groups = {UserCreateValidation.class}, message = "Field email can not be blank")
    @Email(groups = {UserCreateValidation.class, UserUpdateValidation.class}, message = "Invalid email")
    private String email;
}
