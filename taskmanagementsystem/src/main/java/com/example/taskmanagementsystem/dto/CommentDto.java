package com.example.taskmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

    private Long id;

    @NotBlank(message = "Text is mandatory")
    private String text;

    @NotNull(message = "Task ID is mandatory")
    private Long taskId;

    private Long authorId;
}


