package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.request.AddCategoryRequest;
import com.example.ecommercebackend.dto.request.CategoryUpdateRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.service.category.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "Get all categories", description = "Paginated list of categories")
    public ResponseEntity<ApiResponse> getAllCategories(
            @ParameterObject @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse("Found!", categoryService.getAllCategories(pageable)));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add category", description = "Create a new category, admin only")
    public ResponseEntity<ApiResponse> addCategory(@Valid @RequestBody AddCategoryRequest name) {
        CategoryDto categoryDto = categoryService.addCategory(name);
        return ResponseEntity.ok(new ApiResponse("Success", categoryDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a single category by UUID")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable UUID id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Category Found", categoryDto));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get category by name", description = "Retrieve a category by its name")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name) {
        CategoryDto categoryDto = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(new ApiResponse("Find!", categoryDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category", description = "Delete a category by ID, admin only")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable UUID id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Delete Success", null));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category", description = "Update category name, admin only")
    public ResponseEntity<ApiResponse> updateCategoryById(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryDto updatedCategoryDto = categoryService.updateCategory(request, id);
        return ResponseEntity.ok(new ApiResponse("Update Success", updatedCategoryDto));
    }
}
