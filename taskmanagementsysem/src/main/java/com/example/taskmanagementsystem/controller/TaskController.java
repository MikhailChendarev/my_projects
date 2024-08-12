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

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto taskDto) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskDto>> getTasksByStatus(@PathVariable Status status, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByStatus(status, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Page<TaskDto>> getTasksByPriority(@PathVariable Priority priority, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByPriority(priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<TaskDto>> getTasksByAuthorId(@PathVariable Long authorId, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByAuthorId(authorId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<Page<TaskDto>> getTasksByAssigneeId(@PathVariable Long assigneeId, Pageable pageable) {
        Page<TaskDto> tasks = taskService.getTasksByAssigneeId(assigneeId, pageable);
        return ResponseEntity.ok(tasks);
    }
}

