package com.example.taskmanagementsystem.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.model.Comment;
import com.example.taskmanagementsystem.model.Task;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.repository.CommentRepository;
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
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

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
    void testAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");
        commentDto.setTaskId(1L);
        commentDto.setAuthorId(1L);
        Task task = Task.builder().id(1L).build();
        User author = User.builder().id(1L).build();
        Comment comment = Comment.builder()
                .text("Test comment")
                .task(task)
                .author(author)
                .build();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto savedCommentDto = commentService.addComment(commentDto);
        assertNotNull(savedCommentDto);
        assertEquals("Test comment", savedCommentDto.getText());
        assertEquals(1L, savedCommentDto.getTaskId());
        assertEquals(1L, savedCommentDto.getAuthorId());
    }

    @Test
    void testGetCommentsByTaskId() {
        Long taskId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        Task task = Task.builder().id(taskId).build();
        User author = User.builder().id(1L).build();
        Comment comment = Comment.builder()
                .text("Test comment")
                .task(task)
                .author(author)
                .build();
        Page<Comment> page = new PageImpl<>(Collections.singletonList(comment));
        when(commentRepository.findByTaskId(taskId, pageable)).thenReturn(page);
        Page<CommentDto> result = commentService.getCommentsByTaskId(taskId, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test comment", result.getContent().get(0).getText());
        assertEquals(taskId, result.getContent().get(0).getTaskId());
        assertEquals(1L, result.getContent().get(0).getAuthorId());
    }
}
