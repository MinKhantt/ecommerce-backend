package com.example.ecommercebackend.mapper;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.request.AddCategoryRequest;
import com.example.ecommercebackend.entity.Category;
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
