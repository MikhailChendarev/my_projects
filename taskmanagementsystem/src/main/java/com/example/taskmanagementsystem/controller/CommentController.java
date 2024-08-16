package com.example.taskmanagementsystem.controller;

import com.example.taskmanagementsystem.dto.CommentDto;
import com.example.taskmanagementsystem.service.CommentService;
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

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByTaskId(@PathVariable Long taskId, Pageable pageable) {
        Page<CommentDto> comments = commentService.getCommentsByTaskId(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}

