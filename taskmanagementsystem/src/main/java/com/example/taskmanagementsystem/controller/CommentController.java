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

/**
 * Controller for managing comments.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Adds a new comment.
     *
     * @param commentDto comment data
     * @return created comment
     */
    @PostMapping
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.addComment(commentDto);
        return ResponseEntity.ok(createdComment);
    }

    /**
     * Retrieves all comments.
     *
     * @return list of comments
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieves comments by task ID.
     *
     * @param taskId task ID
     * @param pageable pagination parameters
     * @return pages of comments
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByTaskId(@PathVariable Long taskId, Pageable pageable) {
        Page<CommentDto> comments = commentService.getCommentsByTaskId(taskId, pageable);
        return ResponseEntity.ok(comments);
    }
}
