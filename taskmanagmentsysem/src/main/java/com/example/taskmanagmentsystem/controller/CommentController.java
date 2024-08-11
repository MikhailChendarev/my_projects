package com.example.taskmanagmentsystem.controller;

import com.example.taskmanagmentsystem.dto.CommentDto;
import com.example.taskmanagmentsystem.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.addComment(commentDto);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByTaskId(@PathVariable Long taskId, Pageable pageable) {
        Page<CommentDto> comments = commentService.getCommentsByTaskId(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}

