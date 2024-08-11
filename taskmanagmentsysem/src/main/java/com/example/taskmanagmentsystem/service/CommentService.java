package com.example.taskmanagmentsystem.service;

import com.example.taskmanagmentsystem.dto.CommentDto;
import com.example.taskmanagmentsystem.model.Comment;
import com.example.taskmanagmentsystem.model.Task;
import com.example.taskmanagmentsystem.model.User;
import com.example.taskmanagmentsystem.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentDto addComment(CommentDto commentDto) {
        Comment comment = mapToEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
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


