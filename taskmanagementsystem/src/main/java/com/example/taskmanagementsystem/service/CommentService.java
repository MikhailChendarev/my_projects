package com.example.taskmanagementsystem.service;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.model.Comment;
import com.example.taskmanagementsystem.model.Task;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
