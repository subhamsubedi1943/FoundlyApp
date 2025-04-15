package com.foundly.app2.service;

import com.foundly.app2.entity.User;
import com.foundly.app2.exception.DuplicateItemException;
import com.foundly.app2.exception.InvalidRequestException;
import com.foundly.app2.exception.UserNotFoundException;
import com.foundly.app2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmployeeId("EMP001");
    }

    @Test
    public void testGetUserById_UserExists() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Optional<User> result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(1);
        assertFalse(result.isPresent());
    }

    @Test
    public void testSaveUser_UserExists() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = new User();
        updatedUser.setUserId(1);
        updatedUser.setName("New Name");
        updatedUser.setUsername("newusername");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPassword("newpassword");

        User result = userService.saveUser(updatedUser);
        assertEquals("New Name", result.getName());
        assertEquals("newusername", result.getUsername());
        assertEquals("newemail@example.com", result.getEmail());
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testSaveUser_UserNotFound() {
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
        User updatedUser = new User();
        updatedUser.setUserId(1);
        assertThrows(UserNotFoundException.class, () -> userService.saveUser(updatedUser));
    }

    @Test
    public void testCreateUserWithRole_DuplicateEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setUsername("uniqueusername");
        newUser.setEmployeeId("EMP002");
        assertThrows(DuplicateItemException.class, () -> userService.createUserWithRole(newUser));
    }

    @Test
    public void testCreateUserWithRole_DuplicateUsername() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User newUser = new User();
        newUser.setEmail("uniqueemail@example.com");
        newUser.setUsername(user.getUsername());
        newUser.setEmployeeId("EMP002");
        assertThrows(DuplicateItemException.class, () -> userService.createUserWithRole(newUser));
    }

    @Test
    public void testCreateUserWithRole_InvalidEmployeeId() {
        User newUser = new User();
        newUser.setEmail("uniqueemail@example.com");
        newUser.setUsername("uniqueusername");
        newUser.setEmployeeId("");
        assertThrows(InvalidRequestException.class, () -> userService.createUserWithRole(newUser));
    }
}