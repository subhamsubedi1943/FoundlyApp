package com.foundly.app2.controller;

import com.foundly.app2.entity.Category;
import com.foundly.app2.service.CategoryService;
import com.foundly.app2.entity.User;
import com.foundly.app2.service.UserService;
import com.foundly.app2.entity.Employee;
import com.foundly.app2.service.EmployeeService;
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
    @Autowired
    private EmployeeService employeeService;

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
    public User createUserWithRole(@RequestBody com.foundly.app2.dto.UserRequestDTO userRequestDTO) {
        return userService.createUserWithRole(userRequestDTO);
    }

    @PutMapping("/users/{userId}")
    public User updateUser(@PathVariable Integer userId, @RequestBody com.foundly.app2.dto.UserRequestDTO userRequestDTO) {
        userRequestDTO.setUserId(userId);
        return userService.updateUser(userRequestDTO);
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

    // Employee CRUD endpoints
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/employees/{empId}")
    public Employee getEmployeeById(@PathVariable String empId) {
        return employeeService.getEmployeeById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
    }

    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody com.foundly.app2.dto.EmployeeCreationDTO employeeCreationDTO) {
        return employeeService.createEmployeeWithUser(employeeCreationDTO);
    }

    @PutMapping("/employees/{empId}")
    public Employee updateEmployee(@PathVariable String empId, @RequestBody Employee employee) {
        return employeeService.updateEmployee(empId, employee);
    }

    @DeleteMapping("/employees/{empId}")
    public void deleteEmployee(@PathVariable String empId) {
        employeeService.deleteEmployee(empId);
    }
}
