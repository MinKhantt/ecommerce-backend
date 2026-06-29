package com.example.ecommercebackend.dto.request;

import com.example.ecommercebackend.entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String brand;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Min(value = 0, message = "Inventory cannot be negative")
    private int inventory;

    private String description;
    private Category category;
}
