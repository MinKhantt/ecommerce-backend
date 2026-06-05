package com.example.shoppingcartapi.service.category;

import com.example.shoppingcartapi.dto.CategoryDto;
import com.example.shoppingcartapi.dto.request.AddCategoryRequest;
import com.example.shoppingcartapi.dto.request.CategoryUpdateRequest;

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
