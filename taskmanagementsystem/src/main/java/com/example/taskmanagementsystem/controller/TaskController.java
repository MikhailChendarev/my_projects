package com.example.taskmanagementsystem.controller;

import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.enums.Priority;
import com.example.taskmanagementsystem.enums.Status;
import com.example.taskmanagementsystem.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing tasks.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new task.
     *
     * @param taskDto task data
     * @return created task
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id task ID
     * @return task
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Updates an existing task.
     *
     * @param id task ID
     * @param taskDto task data
     * @return updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id task ID
     * @return empty response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all tasks.
     *
     * @return list of tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves tasks by their status.
     *
     * @param status task status
     * @param pageable pagination parameters
     * @return pages of tasks
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskDto>> getTasksByStatus(@PathVariable Status status, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByStatus(status, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves tasks by their priority.
     *
     * @param priority task priority
     * @param pageable pagination parameters
     * @return pages of tasks
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<Page<TaskDto>> getTasksByPriority(@PathVariable Priority priority, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByPriority(priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves tasks by the author's ID.
     *
     * @param authorId author ID
     * @param pageable pagination parameters
     * @return pages of tasks
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<TaskDto>> getTasksByAuthorId(@PathVariable Long authorId, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByAuthorId(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves tasks by the assignee's ID.
     *
     * @param assigneeId assignee ID
     * @param pageable pagination parameters
     * @return pages of tasks
     */
    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<Page<TaskDto>> getTasksByAssigneeId(@PathVariable Long assigneeId, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByAssigneeId(assigneeId, pageable);
        return ResponseEntity.ok(tasks);
    }
}
