package com.example.ecommercebackend.service.category;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.request.AddCategoryRequest;
import com.example.ecommercebackend.dto.request.CategoryUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {

    CategoryDto getCategoryById(UUID id);

    CategoryDto getCategoryByName(String name);

    List<CategoryDto> getAllCategories();

    CategoryDto addCategory(AddCategoryRequest category);

    CategoryDto updateCategory(CategoryUpdateRequest category, UUID id);

    void  deleteCategoryById(UUID id);


}
