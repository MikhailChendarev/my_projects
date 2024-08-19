package com.example.taskmanagementsystem.controller;

import com.example.taskmanagementsystem.dto.UpdatePasswordDto;
import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     *
     * @param userDto user data
     * @return created user
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email user's email
     * @return user
     */
    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates the user's password.
     *
     * @param id user ID
     * @param updatePasswordDto password update data
     * @return updated user
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<UserDto> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        UserDto updatedUser = userService.updatePassword(id, updatePasswordDto.getCurrentPassword(), updatePasswordDto.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }
}

