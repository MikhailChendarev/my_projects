package com.example.taskmanagmentsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class UpdatePasswordDto {

    @NotBlank(message = "Current password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String currentPassword;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String newPassword;
}
