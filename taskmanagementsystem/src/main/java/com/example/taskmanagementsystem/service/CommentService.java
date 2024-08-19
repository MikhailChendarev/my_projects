package com.example.taskmanagementsystem.service;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.exception.ResourceNotFoundException;
import com.example.taskmanagementsystem.model.Comment;
import com.example.taskmanagementsystem.model.Task;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.repository.CommentRepository;
import com.example.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentDto addComment(CommentDto commentDto) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        commentDto.setAuthorId(currentUser.getId());
        log.info("Author ID set to: {}", commentDto.getAuthorId());
        Comment comment = mapToEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    public List<CommentDto> getAllComments() {
        return commentRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<CommentDto> getCommentsByTaskId(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskId(taskId, pageable).map(this::mapToDto);
    }

    private Comment mapToEntity(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .task(Task.builder().id(commentDto.getTaskId()).build())
                .author(User.builder().id(commentDto.getAuthorId()).build())
                .build();
    }

    private CommentDto mapToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setTaskId(comment.getTask().getId());
        commentDto.setAuthorId(comment.getAuthor().getId());
        return commentDto;
    }
}
