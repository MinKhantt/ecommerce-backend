package com.example.ecommercebackend.dto.request;

import com.example.ecommercebackend.entity.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
}
