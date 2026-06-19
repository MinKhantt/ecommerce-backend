package com.example.ecommercebackend.service.product;

import com.example.ecommercebackend.dto.ProductDto;
import com.example.ecommercebackend.dto.request.AddProductRequest;
import com.example.ecommercebackend.dto.request.ProductUpdateRequest;
import com.example.ecommercebackend.dto.response.ProductListResponse;

import java.util.UUID;

public interface IProductService {
    ProductDto createProduct(AddProductRequest request);
    ProductListResponse getAllProducts();
    ProductDto getProductById(UUID id);
    ProductDto updateProduct(ProductUpdateRequest request, UUID productId);
    void deleteProductById(UUID id);

    ProductListResponse getProductsByCategory(String category);
    ProductListResponse getProductsByBrand(String brand);
    ProductListResponse getProductsByCategoryAndBrand(String category, String brand);
    ProductListResponse getProductByName(String name);
    ProductListResponse getProductByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);
}
