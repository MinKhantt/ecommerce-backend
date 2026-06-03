package com.example.shoppingcartapi.controller;

import com.example.shoppingcartapi.dto.ProductDto;
import com.example.shoppingcartapi.dto.request.AddProductRequest;
import com.example.shoppingcartapi.dto.request.ProductUpdateRequest;
import com.example.shoppingcartapi.dto.response.ApiResponse;
import com.example.shoppingcartapi.dto.response.ProductListResponse;
import com.example.shoppingcartapi.exception.AlreadyExistsException;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        log.info("Fetching all products");
        ProductListResponse productDtoList = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse("success", productDtoList));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable UUID productId) {
        try {
            log.info("Fetching product with id: {}", productId);
            ProductDto productDto = productService.getProductById(productId);
            return ResponseEntity.ok(new ApiResponse("success", productDto));
        } catch (ResourceNotFoundException e) {
            log.warn("Product with id: {} not found", productId);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@RequestBody AddProductRequest request) {
        log.info("Received request to create product: {}", request.getName());
        try {
            ProductDto productDto = productService.createProduct(request);
            return ResponseEntity.ok(new ApiResponse("Create product success", productDto));
        } catch (AlreadyExistsException e) {
            log.warn("Product with name={} and brand={} already exists", request.getName(), request.getBrand());
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(
            @RequestBody ProductUpdateRequest request,
            @PathVariable UUID productId
    ) {
        try {
            log.info("Updating product with id {}", productId);
            ProductDto productDto = productService.updateProduct(request, productId);
            return ResponseEntity.ok(new ApiResponse("Update success", productDto));
        } catch (ResourceNotFoundException e) {
            log.warn("Product with id: {} not found during update", productId);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable UUID productId
    ) {
        try {
            log.info("Deleting product with id {}", productId);
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Delete success", productId));
        } catch (ResourceNotFoundException e) {
            log.warn("Product with id {} not found during delete", productId);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Brand & Name
    @GetMapping("/by-brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name
    ) {
        try {
            log.info("Fetching products by brand={} and name={}", brand, name);
            ProductListResponse productDtoList = productService.getProductByBrandAndName(brand, name);

            if (productDtoList.getProducts().isEmpty()) {
                log.warn("No products found for brand={} and name={}", brand, name);
                return ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No Products found!", null));
            }
            return ResponseEntity.ok(new ApiResponse("success", productDtoList));
        } catch (Exception e) {
            log.error("Unexpected error while fetching products by brand={} and name={}", brand, name, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Category & Brand
    @GetMapping("/by-category-and-brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(
            @RequestParam  String category,
            @RequestParam  String brand
    ) {
        try {
            log.info("Fetching products by category={} and brand={}", category, brand);
            ProductListResponse productDtoList = productService.getProductsByCategoryAndBrand(category, brand);

            if (productDtoList.getProducts().isEmpty()) {
                log.warn("No products found for category={} and brand={}", category, brand);
                return ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No Products found!", null));
            }

            return ResponseEntity.ok(new ApiResponse("success", productDtoList));
        } catch (Exception e) {
            log.error("Unexpected error while fetching products by category={} and brand={}", category, brand, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Name
    @GetMapping("/by-name")
    public ResponseEntity<ApiResponse> getProductByName(
            @RequestParam  String name
    ) {
        try {
            log.info("Fetching products by name={}", name);
            ProductListResponse productDtoList = productService.getProductByName(name);

            if (productDtoList.getProducts().isEmpty()) {
                log.warn("No products found for name={}", name);
                return ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No Products found!", null));
            }

            return ResponseEntity.ok(new ApiResponse("success", productDtoList));
        } catch (Exception e) {
            log.error("Unexpected error while fetching products by name={}", name, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Brand
    @GetMapping("/by-brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(
            @RequestParam String brand
    ) {
        try {
            log.info("Fetching products by brand={}", brand);
            ProductListResponse productDtoList = productService.getProductsByBrand(brand);

            if (productDtoList.getProducts().isEmpty()) {
                log.warn("No products found for brand={}", brand);
                return ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No Products found!", null));
            }

            return ResponseEntity.ok(new ApiResponse("success", productDtoList));
        } catch (Exception e) {
            log.error("Unexpected error while fetching products by brand={}", brand, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Category
    @GetMapping("/by-category")
    public ResponseEntity<ApiResponse> getProductsByCategory(
            @RequestParam String category
    ) {
        try {
            log.info("Fetching products by category={}", category);
            ProductListResponse productDtoList = productService.getProductsByCategory(category);

            if (productDtoList.getProducts().isEmpty()) {
                log.warn("No products found for category={}", category);
                return ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No Products found!", null));
            }

            return ResponseEntity.ok(new ApiResponse("success", productDtoList));
        } catch (Exception e) {
            log.error("Unexpected error while fetching products by category={}", category, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Brand & Name
    @GetMapping("/count/by-brand-and-name")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name
    ) {
        try {
            log.info("Counting products by brand={} and name={}", brand, name);
            var productCount = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Product Count!", productCount));
        } catch (Exception e) {
            log.error("Unexpected error while counting products by brand={} and name={}", brand, name, e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
