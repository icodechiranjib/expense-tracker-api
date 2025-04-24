package com.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expensetracker.dto.LoginRequest;
import com.expensetracker.dto.LoginResponse;
import com.expensetracker.dto.RegisterRequest;
import com.expensetracker.dto.RegisterResponse;
import com.expensetracker.entity.Role;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.RoleRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.util.JwtUtil;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("testuser@test.com");
        request.setPassword("test");
        request.setRole("USER");

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setEmail("testuser@test.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(request.getRole())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        RegisterResponse response = authService.register(request);

        assertEquals("User registered successfully", response.getMessage());
        assertEquals("testuser@test.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("testuser@test.com");
        request.setPassword("test");
        request.setRole("USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_RoleNotFound() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("testuser@test.com");
        request.setPassword("test");
        request.setRole("ADMIN");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(request.getRole())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("testuser@test.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("testuser@test.com");
        user.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("mocked-jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("mocked-jwt-token", response.getToken());
        verify(jwtUtil).generateToken(user.getEmail());
    }

    @Test
    void testLogin_InvalidEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("testuser@test.com");
        request.setPassword("wrongpassword");
        
        User user = new User();
        user.setEmail("testuser@test.com");
        user.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.login(request));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}