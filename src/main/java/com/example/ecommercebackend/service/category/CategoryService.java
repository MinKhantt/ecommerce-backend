package com.example.ecommercebackend.service.category;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.request.AddCategoryRequest;
import com.example.ecommercebackend.dto.request.CategoryUpdateRequest;
import com.example.ecommercebackend.exception.AlreadyExistsException;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.entity.Category;
import com.example.ecommercebackend.mapper.CategoryMapper;
import com.example.ecommercebackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::categoryToCategoryDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public CategoryDto getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::categoryToCategoryDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::categoryToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto addCategory(AddCategoryRequest request) {
        if(categoryRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Category already exists: " + request.getName());
        }

        Category category = categoryMapper.addCategoryRequestToCategory(request);
        categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryUpdateRequest request, UUID id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());

        categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    public void deleteCategoryById(UUID id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    throw new ResourceNotFoundException("Category not found!");
                });
    }
}
