package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.ProductDto;
import com.example.ecommercebackend.dto.request.AddProductRequest;
import com.example.ecommercebackend.dto.request.ProductUpdateRequest;
import com.example.ecommercebackend.dto.response.ApiResponse;
import com.example.ecommercebackend.dto.response.ProductListResponse;
import com.example.ecommercebackend.service.product.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Paginated list of products")
    public ResponseEntity<ApiResponse> getAllProducts(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Fetching products page {} with size {}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(new ApiResponse("success", productService.getAllProducts(pageable)));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product by UUID")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable UUID productId) {
        log.info("Fetching product with id: {}", productId);
        ProductDto productDto = productService.getProductById(productId);
        return ResponseEntity.ok(new ApiResponse("success", productDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product, admin only")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody AddProductRequest request) {
        log.info("Received request to create product: {}", request.getName());
        ProductDto productDto = productService.createProduct(request);
        return ResponseEntity.ok(new ApiResponse("Create product success", productDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/product/{productId}")
    @Operation(summary = "Update product", description = "Update product details, admin only")
    public ResponseEntity<ApiResponse> updateProduct(
            @Valid @RequestBody ProductUpdateRequest request,
            @PathVariable UUID productId
    ) {
        log.info("Updating product with id {}", productId);
        ProductDto productDto = productService.updateProduct(request, productId);
        return ResponseEntity.ok(new ApiResponse("Update success", productDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/product/{productId}")
    @Operation(summary = "Delete product", description = "Delete a product by ID, admin only")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable UUID productId
    ) {
        log.info("Deleting product with id {}", productId);
        productService.deleteProductById(productId);
        return ResponseEntity.ok(new ApiResponse("Delete success", productId));
    }

    // Brand & Name
    @GetMapping("/by-brand-and-name")
    @Operation(summary = "Get products by brand and name", description = "Filter products by brand and name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name
    ) {
        log.info("Fetching products by brand={} and name={}", brand, name);
        ProductListResponse productDtoList = productService.getProductByBrandAndName(brand, name);

        if (productDtoList.getProducts().isEmpty()) {
            log.warn("No products found for brand={} and name={}", brand, name);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No Products found!", null));
        }
        return ResponseEntity.ok(new ApiResponse("success", productDtoList));
    }

    // Category & Brand
    @GetMapping("/by-category-and-brand")
    @Operation(summary = "Get products by category and brand", description = "Filter products by category name and brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(
            @RequestParam  String category,
            @RequestParam  String brand
    ) {
        log.info("Fetching products by category={} and brand={}", category, brand);
        ProductListResponse productDtoList = productService.getProductsByCategoryAndBrand(category, brand);

        if (productDtoList.getProducts().isEmpty()) {
            log.warn("No products found for category={} and brand={}", category, brand);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No Products found!", null));
        }

        return ResponseEntity.ok(new ApiResponse("success", productDtoList));
    }

    // Name
    @GetMapping("/by-name")
    @Operation(summary = "Get products by name", description = "Filter products by name")
    public ResponseEntity<ApiResponse> getProductByName(
            @RequestParam  String name
    ) {
        log.info("Fetching products by name={}", name);
        ProductListResponse productDtoList = productService.getProductByName(name);

        if (productDtoList.getProducts().isEmpty()) {
            log.warn("No products found for name={}", name);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No Products found!", null));
        }

        return ResponseEntity.ok(new ApiResponse("success", productDtoList));
    }

    // Brand
    @GetMapping("/by-brand")
    @Operation(summary = "Get products by brand", description = "Filter products by brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(
            @RequestParam String brand
    ) {
        log.info("Fetching products by brand={}", brand);
        ProductListResponse productDtoList = productService.getProductsByBrand(brand);

        if (productDtoList.getProducts().isEmpty()) {
            log.warn("No products found for brand={}", brand);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No Products found!", null));
        }

        return ResponseEntity.ok(new ApiResponse("success", productDtoList));
    }

    // Category
    @GetMapping("/by-category")
    @Operation(summary = "Get products by category", description = "Filter products by category name")
    public ResponseEntity<ApiResponse> getProductsByCategory(
            @RequestParam String category
    ) {
        log.info("Fetching products by category={}", category);
        ProductListResponse productDtoList = productService.getProductsByCategory(category);

        if (productDtoList.getProducts().isEmpty()) {
            log.warn("No products found for category={}", category);
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No Products found!", null));
        }

        return ResponseEntity.ok(new ApiResponse("success", productDtoList));
    }

    // Brand & Name
    @GetMapping("/count/by-brand-and-name")
    @Operation(summary = "Count products by brand and name", description = "Count products matching brand and name")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(
            @RequestParam String brand,
            @RequestParam String name
    ) {
        log.info("Counting products by brand={} and name={}", brand, name);
        var productCount = productService.countProductsByBrandAndName(brand, name);
        return ResponseEntity.ok(new ApiResponse("Product Count!", productCount));
    }
}
