package com.example.shoppingcartapi.service.product;

import com.example.shoppingcartapi.dto.ProductDto;
import com.example.shoppingcartapi.dto.request.AddProductRequest;
import com.example.shoppingcartapi.dto.request.ProductUpdateRequest;
import com.example.shoppingcartapi.dto.response.ProductListResponse;
import com.example.shoppingcartapi.entity.Product;

import java.util.List;
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
