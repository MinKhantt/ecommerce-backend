package com.example.shoppingcartapi.service.product;

import com.example.shoppingcartapi.dto.ProductDto;
import com.example.shoppingcartapi.dto.request.AddProductRequest;
import com.example.shoppingcartapi.dto.request.ProductUpdateRequest;
import com.example.shoppingcartapi.dto.response.ProductListResponse;
import com.example.shoppingcartapi.entity.Product;

import java.util.List;

public interface IProductService {
    ProductDto createProduct(AddProductRequest request);
    ProductListResponse getAllProducts();
    ProductDto getProductById(Long id);
    ProductDto updateProduct(ProductUpdateRequest request, Long productId);
    void deleteProductById(Long id);

    ProductListResponse getProductsByCategory(String category);
    ProductListResponse getProductsByBrand(String brand);
    ProductListResponse getProductsByCategoryAndBrand(String category, String brand);
    ProductListResponse getProductByName(String name);
    ProductListResponse getProductByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);
}
