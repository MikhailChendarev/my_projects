package com.example.taskmanagmentsystem.service;

import com.example.taskmanagmentsystem.dto.TaskDto;
import com.example.taskmanagmentsystem.enums.Priority;
import com.example.taskmanagmentsystem.enums.Status;
import com.example.taskmanagmentsystem.exception.ResourceNotFoundException;
import com.example.taskmanagmentsystem.model.Task;
import com.example.taskmanagmentsystem.model.User;
import com.example.taskmanagmentsystem.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskDto createTask(TaskDto taskDto) {
        Task task = mapToEntity(taskDto);
        Task savedTask = taskRepository.save(task);
        return mapToDto(savedTask);
    }

    public Page<TaskDto> getTasksByAuthorId(Long authorId, Pageable pageable) {
        return taskRepository.findByAuthorId(authorId, pageable).map(this::mapToDto);
    }

    public Page<TaskDto> getTasksByAssigneeId(Long assigneeId, Pageable pageable) {
        return taskRepository.findByAssigneeId(assigneeId, pageable).map(this::mapToDto);
    }

    public void deleteTask(Long id) {
        taskRepository.delete(findByIdOrThrow(id));
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        findByIdOrThrow(id);
        Task updatedTask = taskRepository.save(mapToEntity(taskDto));
        return mapToDto(updatedTask);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        Task task = findByIdOrThrow(id);
        return mapToDto(task);
    }

    private Task findByIdOrThrow(Long id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + id + " not found."));
    }

    public Page<TaskDto> getTasksByStatus(Status status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable).map(this::mapToDto);
    }

    public Page<TaskDto> getTasksByPriority(Priority priority, Pageable pageable) {
        return taskRepository.findByPriority(priority, pageable).map(this::mapToDto);
    }

    private Task mapToEntity(TaskDto taskDto) {
        return Task.builder()
                .id(taskDto.getId())
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus())
                .priority(taskDto.getPriority())
                .author(User.builder().id(taskDto.getAuthorId()).build())
                .assignee(User.builder().id(taskDto.getAssigneeId()).build())
                .build();
    }

    private TaskDto mapToDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        taskDto.setPriority(task.getPriority());
        taskDto.setAuthorId(task.getAuthor().getId());
        taskDto.setAssigneeId(task.getAssignee().getId());
        return taskDto;
    }
}
