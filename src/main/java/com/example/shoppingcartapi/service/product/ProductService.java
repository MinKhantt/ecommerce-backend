package com.example.shoppingcartapi.service.product;

import com.example.shoppingcartapi.dto.ProductDto;
import com.example.shoppingcartapi.dto.request.AddProductRequest;
import com.example.shoppingcartapi.dto.request.ProductUpdateRequest;
import com.example.shoppingcartapi.dto.response.ProductListResponse;
import com.example.shoppingcartapi.exception.AlreadyExistsException;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.entity.Category;
import com.example.shoppingcartapi.entity.Product;
import com.example.shoppingcartapi.mapper.ProductMapper;
import com.example.shoppingcartapi.repository.CategoryRepository;
import com.example.shoppingcartapi.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    @CacheEvict(value = "productQueries", allEntries = true)
    @Override
    public ProductDto createProduct(AddProductRequest request) {
        log.debug("Starting product creation for name={} and brand={}", request.getName(), request.getBrand());

        if (productRepository.existsByNameAndBrand(request.getName(), request.getBrand())) {
            log.debug("Product creation stopped because product already exists for name={} and brand={}",
                    request.getName(), request.getBrand());
            throw new AlreadyExistsException(request.getName() + " " + request.getBrand() + " already exists!");
        }

        // Fetch or create the category
        Category category = categoryRepository.findByName(request.getCategory());
        if (category == null) {
            Category newCategory = new Category(request.getCategory());
            category = categoryRepository.save(newCategory);
            log.debug("Created new category with id={} and name={}", category.getId(), category.getName());
        } else {
            log.debug("Using existing category with id={} and name={}", category.getId(), category.getName());
        }

        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        log.debug("Created product with id={}, name={}, brand={}, category={}",
                savedProduct.getId(), savedProduct.getName(), savedProduct.getBrand(), category.getName());
        return productMapper.ProductToProductDto(savedProduct);
    }

    @Cacheable(value = "productQueries", key = "'all'")
    @Override
    public ProductListResponse getAllProducts() {
        log.debug("Retrieving all products");
        List<ProductDto> products = productRepository.findAll()
                .stream()
                .map(productMapper::ProductToProductDto)
                .toList();

        log.debug("Retrieved {} products", products.size());
        return new ProductListResponse(products);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductDto getProductById(UUID id) {
        log.debug("Looking up product by id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(()-> {
                    log.debug("Product lookup failed for id={}", id);
                    return new ResourceNotFoundException("Product not found!");
                });

        log.debug("Found product with id={}, name={}, brand={}", product.getId(), product.getName(), product.getBrand());
        return productMapper.ProductToProductDto(product);
    }

    @Transactional
    @Override
    @CachePut(value = "products", key = "#productId")
    @CacheEvict(value = "productQueries", allEntries = true)
    public ProductDto updateProduct(ProductUpdateRequest request, UUID productId) {

        log.debug("Starting product update for id={}", productId);
        Product existingProduct =  productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.debug("Product update stopped because product id={} was not found", productId);
                    return new ResourceNotFoundException("Product not found!");
                });

        // Fetch or create the category
        Category category = categoryRepository.findByName(request.getCategory().getName());
        if (category == null) {
            Category newCategory = new Category(request.getCategory().getName());
            category = categoryRepository.save(newCategory);
            log.debug("Created new category with id={} and name={} during product update", category.getId(), category.getName());
        } else {
            log.debug("Using existing category with id={} and name={} during product update", category.getId(), category.getName());
        }

        productMapper.toUpdateProduct(request, existingProduct);
        existingProduct.setCategory(category);
        Product savedProduct = productRepository.save(existingProduct);
        log.debug("Updated product with id={}, name={}, brand={}, category={}",
                savedProduct.getId(), savedProduct.getName(), savedProduct.getBrand(), category.getName());
        return productMapper.ProductToProductDto(existingProduct);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#productId"),
            @CacheEvict(value = "productQueries", allEntries = true)
    })
    @Override
    public void deleteProductById(UUID productId) {
        log.debug("Starting product deletion for id={}", productId);
        productRepository.findById(productId)
                        .ifPresentOrElse(
                                product -> {
                                    productRepository.delete(product);
                                    log.debug("Deleted product with id={}, name={}, brand={}",
                                            product.getId(), product.getName(), product.getBrand());
                                },
                                ()-> {
                                    log.debug("Product deletion stopped because product id={} was not found", productId);
                                    throw new ResourceNotFoundException("Product not found");
                                }
                        );
    }

    @Override
    @Cacheable(value = "productQueries", key = "'category:' + #category")
    public ProductListResponse getProductsByCategory(String category) {
        log.debug("Retrieving products by category={}", category);
        List<ProductDto> products = productRepository.findByCategoryName(category)
                .stream()
                .map(productMapper::ProductToProductDto)
                .toList();

        log.debug("Retrieved {} products for category={}", products.size(), category);
        return new ProductListResponse(products);
    }

    @Override
    @Cacheable(value = "productQueries", key = "'brand:' + #brand")
    public ProductListResponse getProductsByBrand(String brand) {
        log.debug("Retrieving products by brand={}", brand);
        List<ProductDto> products = productRepository.findByBrand(brand)
                .stream()
                .map(productMapper::ProductToProductDto)
                .toList();

        log.debug("Retrieved {} products for brand={}", products.size(), brand);
        return new ProductListResponse(products);
    }

    @Override
    @Cacheable(value = "productQueries", key = "'category_brand:' + #category + ':' + #brand")
    public ProductListResponse getProductsByCategoryAndBrand(String category, String brand) {

        log.debug("Retrieving products by category={} and brand={}", category, brand);
        List<ProductDto> products = productRepository.findByCategoryNameAndBrand(category, brand)
                .stream()
                .map(productMapper::ProductToProductDto)
                .toList();

        log.debug("Retrieved {} products for category={} and brand={}", products.size(), category, brand);
        return new ProductListResponse(products);
    }

    @Override
    @Cacheable(value = "productQueries", key = "'name:' + #name")
    public ProductListResponse getProductByName(String name) {

        log.debug("Retrieving products by name={}", name);
        List<ProductDto> products = productRepository.findByName(name)
                .stream()
                .map(productMapper::ProductToProductDto)
                .toList();

        log.debug("Retrieved {} products for name={}", products.size(), name);
        return new ProductListResponse(products);
    }

    @Override
    @Cacheable(value = "productQueries", key = "'brand_name:' + #brand + ':' + #name")
    public ProductListResponse getProductByBrandAndName(String brand, String name) {

        log.debug("Retrieving products by brand={} and name={}", brand, name);
        List<ProductDto> products = productRepository.findByBrandAndName(brand, name)
                .stream()
                .map(productMapper::ProductToProductDto)
                .toList();

        log.debug("Retrieved {} products for brand={} and name={}", products.size(), brand, name);
        return new ProductListResponse(products);
    }

    @Override
    @Cacheable(value = "productQueries", key = "'count_brand_name:' + #brand + ':' + #name")
    public Long countProductsByBrandAndName(String brand, String name) {

        log.debug("Counting products by brand={} and name={}", brand, name);
        Long count = productRepository.countByBrandAndName(brand, name);

        log.debug("Counted {} products for brand={} and name={}", count, brand, name);
        return count;
    }
}
