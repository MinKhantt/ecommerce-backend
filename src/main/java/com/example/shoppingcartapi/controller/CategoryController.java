package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.CategoryDto;
import com.example.shoppingcartapi.dto.request.AddCategoryRequest;
import com.example.shoppingcartapi.dto.request.CategoryUpdateRequest;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.exception.AlreadyExistsException;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final ICategoryService categoryService;

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<CategoryDto> categoriesDto = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse("Found!", categoriesDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error: ", INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> addCategory(@RequestBody AddCategoryRequest name) {
        try {
            CategoryDto categoryDto = categoryService.addCategory(name);
            return ResponseEntity.ok(new ApiResponse("Success", categoryDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable UUID id) {
        try {
            CategoryDto categoryDto = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Category Found", categoryDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name) {
        try {
            CategoryDto categoryDto = categoryService.getCategoryByName(name);
            return ResponseEntity.ok(new ApiResponse("Find!", categoryDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategoryById(@PathVariable UUID id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Delete Success", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategoryById(
            @PathVariable UUID id,
            @RequestBody CategoryUpdateRequest request
    ) {
        try {
            CategoryDto updatedCategoryDto = categoryService.updateCategory(request, id);
            return ResponseEntity.ok(new ApiResponse("Update Success", updatedCategoryDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
