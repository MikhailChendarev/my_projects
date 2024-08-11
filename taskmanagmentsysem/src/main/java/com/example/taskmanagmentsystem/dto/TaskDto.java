package com.example.taskmanagmentsystem.dto;

import com.example.taskmanagmentsystem.enums.Priority;
import com.example.taskmanagmentsystem.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TaskDto {

    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    @NotNull(message = "Status is mandatory")
    private Status status;

    @NotNull(message = "Priority is mandatory")
    private Priority priority;

    @NotNull(message = "Author ID is mandatory")
    private Long authorId;

    private Long assigneeId;
}
