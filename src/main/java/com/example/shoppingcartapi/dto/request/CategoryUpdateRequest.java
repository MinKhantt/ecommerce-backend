package com.example.shoppingcartapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 2, max = 50)
    private String name;
}
