package com.example.taskmanagementsystem.service;

import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.enums.Priority;
import com.example.taskmanagementsystem.enums.Status;
import com.example.taskmanagementsystem.exception.ResourceNotFoundException;
import com.example.taskmanagementsystem.model.Task;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.repository.TaskRepository;
import com.example.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing tasks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new task.
     *
     * @param taskDto task data
     * @return created task
     */
    public TaskDto createTask(TaskDto taskDto) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        taskDto.setAuthorId(currentUser.getId());
        log.info("Author ID set to: {}", taskDto.getAuthorId());
        Task task = mapToEntity(taskDto);
        Task savedTask = taskRepository.save(task);
        return mapToDto(savedTask);
    }

    /**
     * Retrieves tasks by author ID.
     *
     * @param authorId author's ID
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    public Page<TaskDto> getTasksByAuthorId(Long authorId, Pageable pageable) {
        return taskRepository.findByAuthorId(authorId, pageable).map(this::mapToDto);
    }

    /**
     * Retrieves tasks by assignee ID.
     *
     * @param assigneeId assignee's ID
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    public Page<TaskDto> getTasksByAssigneeId(Long assigneeId, Pageable pageable) {
        return taskRepository.findByAssigneeId(assigneeId, pageable).map(this::mapToDto);
    }

    /**
     * Deletes a task by ID.
     *
     * @param id task ID
     */
    public void deleteTask(Long id) {
        taskRepository.delete(findByIdOrThrow(id));
    }

    /**
     * Updates a task.
     *
     * @param id task ID
     * @param taskDto task data
     * @return updated task
     */
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        findByIdOrThrow(id);
        Task updatedTask = taskRepository.save(mapToEntity(taskDto));
        return mapToDto(updatedTask);
    }

    /**
     * Retrieves all tasks.
     *
     * @return list of all tasks
     */
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Retrieves a task by ID.
     *
     * @param id task ID
     * @return task data
     */
    public TaskDto getTaskById(Long id) {
        Task task = findByIdOrThrow(id);
        return mapToDto(task);
    }

    private Task findByIdOrThrow(Long id) {
        return taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + id + " not found."));
    }

    /**
     * Retrieves tasks by status.
     *
     * @param status task status
     * @param pageable pagination information
     * @return paginated list of tasks
     */
    public Page<TaskDto> getTasksByStatus(Status status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable).map(this::mapToDto);
    }

    /**
     * Retrieves tasks by priority.
     *
     * @param priority task priority
     * @param pageable pagination information
     * @return paginated list of tasks
     */
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
