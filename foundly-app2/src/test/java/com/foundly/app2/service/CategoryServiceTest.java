package com.foundly.app2.service;

import com.foundly.app2.entity.Category;
import com.foundly.app2.exception.CategoryNotFoundException;
import com.foundly.app2.exception.InvalidRequestException;
import com.foundly.app2.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Test Category");
    }

    @Test
    public void testGetCategoryById_CategoryExists() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        Optional<Category> result = categoryService.getCategoryById(1);
        assertTrue(result.isPresent());
        assertEquals("Test Category", result.get().getCategoryName());
    }

    @Test
    public void testGetCategoryById_CategoryNotFound() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Category> result = categoryService.getCategoryById(1);
        assertFalse(result.isPresent());
    }

    @Test
    public void testCreateCategory_ValidCategory() {
        when(categoryRepository.existsByCategoryName(category.getCategoryName())).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        Category result = categoryService.createCategory(category);
        assertEquals("Test Category", result.getCategoryName());
    }

    @Test
    public void testCreateCategory_NullOrEmptyName() {
        Category invalidCategory = new Category();
        invalidCategory.setCategoryName("");
        assertThrows(InvalidRequestException.class, () -> categoryService.createCategory(invalidCategory));
    }

    @Test
    public void testCreateCategory_DuplicateName() {
        when(categoryRepository.existsByCategoryName(category.getCategoryName())).thenReturn(true);
        assertThrows(InvalidRequestException.class, () -> categoryService.createCategory(category));
    }

    @Test
    public void testUpdateCategory_CategoryExists() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Category");

        Category result = categoryService.updateCategory(1, updatedCategory);
        assertEquals("Updated Category", result.getCategoryName());
    }

    @Test
    public void testUpdateCategory_CategoryNotFound() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Category");
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1, updatedCategory));
    }

    @Test
    public void testUpdateCategory_NullOrEmptyName() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("");
        assertThrows(InvalidRequestException.class, () -> categoryService.updateCategory(1, updatedCategory));
    }
}