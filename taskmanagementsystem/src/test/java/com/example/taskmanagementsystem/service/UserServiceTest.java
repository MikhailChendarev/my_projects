package com.example.taskmanagementsystem.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.taskmanagementsystem.dto.UserDto;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto savedUserDto = userService.createUser(userDto);
        assertNotNull(savedUserDto);
        assertEquals("test@example.com", savedUserDto.getEmail());
        assertEquals("encodedPassword", savedUserDto.getPassword());
    }

    @Test
    void testGetUserByEmail() {
        String email = "test@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUserByEmail(email);
        assertNotNull(userDto);
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void testUpdatePassword() {
        Long userId = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("encodedCurrentPassword")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto updatedUserDto = userService.updatePassword(userId, currentPassword, newPassword);
        assertNotNull(updatedUserDto);
        assertEquals("encodedNewPassword", updatedUserDto.getPassword());
    }
}

