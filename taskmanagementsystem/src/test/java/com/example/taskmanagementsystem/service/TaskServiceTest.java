package com.example.taskmanagementsystem.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.taskmanagementsystem.dto.TaskDto;
import com.example.taskmanagementsystem.enums.Priority;
import com.example.taskmanagementsystem.enums.Status;
import com.example.taskmanagementsystem.model.Task;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.repository.TaskRepository;
import com.example.taskmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test task");
        taskDto.setDescription("Test description");
        taskDto.setStatus(Status.COMPLETED);
        taskDto.setPriority(Priority.HIGH);
        taskDto.setAuthorId(1L);
        taskDto.setAssigneeId(2L);
        User author = User.builder().id(1L).build();
        User assignee = User.builder().id(2L).build();
        Task task = Task.builder()
                .id(1L)
                .title("Test task")
                .description("Test description")
                .status(Status.COMPLETED)
                .priority(Priority.HIGH)
                .author(author)
                .assignee(assignee)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(author));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        TaskDto savedTaskDto = taskService.createTask(taskDto);
        assertNotNull(savedTaskDto);
        assertEquals("Test task", savedTaskDto.getTitle());
        assertEquals("Test description", savedTaskDto.getDescription());
        assertEquals(Status.COMPLETED, savedTaskDto.getStatus());
        assertEquals(Priority.HIGH, savedTaskDto.getPriority());
        assertEquals(1L, savedTaskDto.getAuthorId());
        assertEquals(2L, savedTaskDto.getAssigneeId());
    }

    @Test
    void testGetTasksByAuthorId() {
        Long authorId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        User author = User.builder().id(authorId).build();
        User assignee = User.builder().id(2L).build();
        Task task = Task.builder()
                .id(1L)
                .title("Test task")
                .description("Test description")
                .status(Status.COMPLETED)
                .priority(Priority.HIGH)
                .author(author)
                .assignee(assignee)
                .build();
        Page<Task> page = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.findByAuthorId(authorId, pageable)).thenReturn(page);
        Page<TaskDto> result = taskService.getTasksByAuthorId(authorId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test task", result.getContent().get(0).getTitle());
        assertEquals("Test description", result.getContent().get(0).getDescription());
        assertEquals(Status.COMPLETED, result.getContent().get(0).getStatus());
        assertEquals(Priority.HIGH, result.getContent().get(0).getPriority());
        assertEquals(authorId, result.getContent().get(0).getAuthorId());
        assertEquals(2L, result.getContent().get(0).getAssigneeId());
    }

    @Test
    void testDeleteTask() {
        Long taskId = 1L;
        User author = User.builder().id(1L).build();
        User assignee = User.builder().id(2L).build();
        Task task = Task.builder()
                .id(taskId)
                .author(author)
                .assignee(assignee)
                .build();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        taskService.deleteTask(taskId);
        verify(taskRepository, times(1)).delete(task);
    }
}
