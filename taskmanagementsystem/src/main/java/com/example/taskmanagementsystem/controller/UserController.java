package com.example.taskmanagementsystem.controller;

import com.example.taskmanagementsystem.dto.UpdatePasswordDto;
import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserDto> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        UserDto updatedUser = userService.updatePassword(id, updatePasswordDto.getCurrentPassword(), updatePasswordDto.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }
}
