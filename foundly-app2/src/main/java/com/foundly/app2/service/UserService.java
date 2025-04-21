package com.foundly.app2.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foundly.app2.dto.EditUserDTO;
import com.foundly.app2.dto.UserLoginRequest;
import com.foundly.app2.dto.UserRegistrationRequest;
import com.foundly.app2.entity.User;
import com.foundly.app2.exception.DuplicateItemException;
import com.foundly.app2.exception.InvalidRequestException;
import com.foundly.app2.exception.UserNotFoundException;
import com.foundly.app2.repository.UserRepository;

@Service
public class UserService {
	@Autowired(required=true)
	private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    //private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    // Get a user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Create or update a user
    public User saveUser(User updatedUser) {
        // Fetch existing user from DB
        User existingUser = userRepository.findById(updatedUser.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Only update allowed editable fields
        existingUser.setName(updatedUser.getName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        // Update password only if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }


    @Autowired
    private ItemReportsService itemReportsService;

    @Autowired
    private TransactionsService transactionsService;

    // Delete a user
    public void deleteUser (Integer userId) {
        // Delete dependent transactions first to avoid foreign key constraint error
        transactionsService.deleteTransactionsByRequesterUserId(userId);
        // Delete dependent item reports
        itemReportsService.deleteItemReportsByUserId(userId);
        // Delete user
        userRepository.deleteById(userId);
    }

    public User registerUser(UserRegistrationRequest request) {
        User user = new User();
        user.setEmployeeId(request.getEmployeeId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Generate a default username using the name (lowercase, no spaces)
        String baseUsername = request.getName().trim().toLowerCase().replaceAll("\\s+", "");
        String username = baseUsername;
        int counter = 1;

        // Ensure uniqueness by checking if username already exists
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        user.setUsername(username); // Set the generated username
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }



    public Optional<User> loginUser(UserLoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }


    // Find a user by ID (new method)
    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    public User promoteToAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRole(User.Role.ADMIN);
        return userRepository.save(user);
    }

    public User demoteFromAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }
    
    public User createAdminUser(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateItemException("Email already exists");
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(User.Role.ADMIN);
        admin.setName("Admin User"); // Default name
        
        return userRepository.save(admin);
    }

    public User createUserWithRole(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateItemException("Email already exists");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateItemException("Username already exists");
        }
        if (user.getEmployeeId() == null || user.getEmployeeId().trim().isEmpty()) {
            throw new InvalidRequestException("Employee ID is required");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
            
        // Update all fields including security
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setUsername(user.getUsername());
        existingUser.setRole(user.getRole());
        existingUser.setSecurity(user.isSecurity()); // This line is correct if Lombok generates standard methods
        existingUser.setEmployeeId(user.getEmployeeId());
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    public EditUserDTO updateUserProfileById(EditUserDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + dto.getUserId()));

        // Verify old password before allowing update
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

        // Update editable fields
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());

        // Update password only if new one is provided
        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userRepository.save(user);

        // Prepare response DTO without password
        EditUserDTO response = new EditUserDTO();
        response.setUserId(user.getUserId());
        response.setEmployeeId(user.getEmployeeId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setOldPassword(""); // Clear sensitive fields
        response.setNewPassword("");

        return response;
    }
}
