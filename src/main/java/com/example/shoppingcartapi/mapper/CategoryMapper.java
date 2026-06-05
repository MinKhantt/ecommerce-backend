package com.example.shoppingcartapi.mapper;

import com.example.shoppingcartapi.dto.CategoryDto;
import com.example.shoppingcartapi.dto.request.AddCategoryRequest;
import com.example.shoppingcartapi.entity.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public Category addCategoryRequestToCategory(AddCategoryRequest addCategoryRequest) {
        return modelMapper.map(addCategoryRequest,Category.class);
    }

    public CategoryDto categoryToCategoryDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    public Category categoryDtoToCategory(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }
}
