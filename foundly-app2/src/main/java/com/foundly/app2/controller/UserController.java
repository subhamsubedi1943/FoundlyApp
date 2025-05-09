package com.foundly.app2.controller;

import com.foundly.app2.entity.User;
import com.foundly.app2.dto.UserRegistrationRequest;
import com.foundly.app2.dto.EditUserDTO;
//import com.foundly.app2.dto.ForgotPasswordDTO;
import com.foundly.app2.dto.UserLoginRequest;
// import com.foundly.app2.exception.DuplicateItemException;
// import com.foundly.app2.exception.InvalidRequestException;
// import com.foundly.app2.exception.UserNotFoundException;
import com.foundly.app2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
//    @GetMapping("/{username}")
//    public Optional<User> getUserByUsername(@PathVariable String username) {
//        return userService.getUserByUsername(username);
//    }

    // Get a user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        User createdUser = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Login a user
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody UserLoginRequest loginRequest) {
        Optional<User> user = userService.loginUser(loginRequest);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    // Create a new user (if you want to keep this method for other purposes)
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        User createdUser = userService.saveUser(user);
//        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//    }

    // Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody com.foundly.app2.dto.UserRequestDTO userRequestDTO) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            userRequestDTO.setUserId(id);
            User updatedUser = userService.updateUser(userRequestDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @GetMapping("/profile/{userId}")
    public ResponseEntity<EditUserDTO> getEditableUserProfile(@PathVariable Integer userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            User u = user.get();
            EditUserDTO dto = new EditUserDTO();
            dto.setUserId(u.getUserId());
            dto.setEmployeeId(u.getEmployeeId()); // Display only
            dto.setName(u.getName());
            dto.setEmail(u.getEmail());
            dto.setUsername(u.getUsername());
            dto.setOldPassword(""); // Clear sensitive info
            dto.setNewPassword("");
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update editable fields of a user profile with old password verification
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody EditUserDTO dto) {
        try {
            EditUserDTO updated = userService.updateUserProfileById(dto);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
//    @PostMapping("/forgot-password")
//    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
//        userService.resetPassword(forgotPasswordDTO);
//        return ResponseEntity.ok("Password reset successful.");
//    }
}
