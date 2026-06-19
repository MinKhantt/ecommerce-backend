package com.example.ecommercebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddCategoryRequest {
        @NotBlank(message = "Category name cannot be empty")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;
}
