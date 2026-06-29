package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.request.AddCategoryRequest;
import com.example.ecommercebackend.dto.request.CategoryUpdateRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.category.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final ICategoryService categoryService;

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryDto> categoriesDto = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse("Found!", categoriesDto));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> addCategory(@Valid @RequestBody AddCategoryRequest name) {
        CategoryDto categoryDto = categoryService.addCategory(name);
        return ResponseEntity.ok(new ApiResponse("Success", categoryDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable UUID id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Category Found", categoryDto));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name) {
        CategoryDto categoryDto = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(new ApiResponse("Find!", categoryDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable UUID id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Delete Success", null));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateCategoryById(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryDto updatedCategoryDto = categoryService.updateCategory(request, id);
        return ResponseEntity.ok(new ApiResponse("Update Success", updatedCategoryDto));
    }
}
