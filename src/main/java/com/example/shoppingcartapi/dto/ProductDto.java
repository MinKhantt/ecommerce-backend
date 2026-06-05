package com.example.shoppingcartapi.dto;

import com.example.shoppingcartapi.entity.Category;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ProductDto implements Serializable {
    private UUID id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private CategoryDto category;
    private List<ImageDto> images;
}
