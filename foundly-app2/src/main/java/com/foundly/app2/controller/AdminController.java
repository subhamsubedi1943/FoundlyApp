package com.foundly.app2.controller;

import com.foundly.app2.entity.Category;
import com.foundly.app2.service.CategoryService;
import com.foundly.app2.entity.User;
import com.foundly.app2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users/{userId}/promote")
    public User promoteToAdmin(@PathVariable Integer userId) {
        return userService.promoteToAdmin(userId);
    }

    @PostMapping("/users/{userId}/demote")
    public User demoteFromAdmin(@PathVariable Integer userId) {
        return userService.demoteFromAdmin(userId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/users")
    public User createUserWithRole(@RequestBody User user) {
        return userService.createUserWithRole(user);
    }

    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable Integer userId, @RequestBody User user) {
        user.setUserId(userId);
        return userService.updateUser(user);
    }
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }
}