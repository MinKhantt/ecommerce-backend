package com.example.ecommercebackend.service.category;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.request.AddCategoryRequest;
import com.example.ecommercebackend.dto.request.CategoryUpdateRequest;
import com.example.ecommercebackend.dto.response.PageResponse;
import com.example.ecommercebackend.entity.Category;
import com.example.ecommercebackend.exception.AlreadyExistsException;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.mapper.CategoryMapper;
import com.example.ecommercebackend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Test")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private AddCategoryRequest addRequest;
    private CategoryUpdateRequest updateRequest;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category("Electronics");
        category.setId(categoryId);

        categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Electronics");

        addRequest = new AddCategoryRequest();
        addRequest.setName("Electronics");

        updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Home Appliances");
    }

    @Nested
    @DisplayName("Get Category Tests")
    class GetCategoryTests {

        @Test
        void shouldGetCategoryById() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDto);

            CategoryDto result = categoryService.getCategoryById(categoryId);

            assertNotNull(result);
            assertEquals(categoryDto.getName(), result.getName());
            verify(categoryRepository).findById(categoryId);
        }

        @Test
        void shouldThrowWhenGetCategoryByIdNotFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
            verify(categoryRepository).findById(categoryId);
        }

        @Test
        void shouldGetCategoryByName() {
            when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
            when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDto);

            CategoryDto result = categoryService.getCategoryByName("Electronics");

            assertNotNull(result);
            assertEquals("Electronics", result.getName());
            verify(categoryRepository).findByName("Electronics");
        }

        @Test
        void shouldGetAllCategoriesPaginated() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Category> page = new PageImpl<>(List.of(category));
            when(categoryRepository.findAll(pageable)).thenReturn(page);
            when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDto);

            PageResponse<CategoryDto> result = categoryService.getAllCategories(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("Electronics", result.getContent().getFirst().getName());
            verify(categoryRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Create Category Tests")
    class CreateCategoryTests {

        @Test
        void shouldAddCategory() {
            when(categoryRepository.existsByName("Electronics")).thenReturn(false);
            when(categoryMapper.addCategoryRequestToCategory(addRequest)).thenReturn(category);
            when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDto);

            CategoryDto result = categoryService.addCategory(addRequest);

            assertNotNull(result);
            assertEquals("Electronics", result.getName());
            verify(categoryRepository).existsByName("Electronics");
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldThrowWhenCategoryAlreadyExists() {
            when(categoryRepository.existsByName("Electronics")).thenReturn(true);

            assertThrows(AlreadyExistsException.class, () -> categoryService.addCategory(addRequest));
            verify(categoryRepository).existsByName("Electronics");
            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update and Delete Category Tests")
    class UpdateDeleteCategoryTests {

        @Test
        void shouldUpdateCategory() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryMapper.categoryToCategoryDto(category)).thenReturn(categoryDto);

            CategoryDto result = categoryService.updateCategory(updateRequest, categoryId);

            assertNotNull(result);
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).save(category);
        }

        @Test
        void shouldDeleteCategoryById() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            doNothing().when(categoryRepository).delete(category);

            categoryService.deleteCategoryById(categoryId);

            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).delete(category);
        }

        @Test
        void shouldThrowWhenDeleteCategoryNotFound() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(categoryId));
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository, never()).delete(any());
        }
    }
}
