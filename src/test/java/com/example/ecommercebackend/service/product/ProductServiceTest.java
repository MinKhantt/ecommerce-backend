package com.example.ecommercebackend.service.product;

import com.example.ecommercebackend.dto.CategoryDto;
import com.example.ecommercebackend.dto.ProductDto;
import com.example.ecommercebackend.dto.request.AddProductRequest;
import com.example.ecommercebackend.dto.request.ProductUpdateRequest;
import com.example.ecommercebackend.dto.response.PageResponse;
import com.example.ecommercebackend.dto.response.ProductListResponse;
import com.example.ecommercebackend.entity.Category;
import com.example.ecommercebackend.entity.Product;
import com.example.ecommercebackend.exception.AlreadyExistsException;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.mapper.ProductMapper;
import com.example.ecommercebackend.repository.CategoryRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Test")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private Category category;
    private CategoryDto categoryDto;
    private AddProductRequest addRequest;
    private ProductUpdateRequest updateRequest;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        category = new Category("Electronics");
        category.setId(categoryId);

        categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Electronics");

        product = new Product("iPhone", "Apple", BigDecimal.valueOf(999), 10, "Smartphone", category);
        product.setId(productId);

        productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setName("iPhone");
        productDto.setBrand("Apple");
        productDto.setPrice(BigDecimal.valueOf(999));
        productDto.setInventory(10);
        productDto.setCategory(categoryDto);

        addRequest = new AddProductRequest();
        addRequest.setName("iPhone");
        addRequest.setBrand("Apple");
        addRequest.setPrice(BigDecimal.valueOf(999));
        addRequest.setInventory(10);
        addRequest.setCategory("Electronics");

        updateRequest = new ProductUpdateRequest();
        updateRequest.setName("iPhone 15");
        updateRequest.setBrand("Apple");
        updateRequest.setPrice(BigDecimal.valueOf(1099));
        updateRequest.setInventory(5);
        updateRequest.setCategory(category);
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        void shouldCreateProduct() {
            when(productRepository.existsByNameAndBrand("iPhone", "Apple")).thenReturn(false);
            when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
            when(productMapper.toProduct(addRequest)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            ProductDto result = productService.createProduct(addRequest);

            assertNotNull(result);
            assertEquals("iPhone", result.getName());
            verify(productRepository).existsByNameAndBrand("iPhone", "Apple");
            verify(categoryRepository).findByName("Electronics");
            verify(productRepository).save(product);
        }

        @Test
        void shouldThrowWhenProductAlreadyExists() {
            when(productRepository.existsByNameAndBrand("iPhone", "Apple")).thenReturn(true);

            assertThrows(AlreadyExistsException.class, () -> productService.createProduct(addRequest));
            verify(productRepository).existsByNameAndBrand("iPhone", "Apple");
            verify(productRepository, never()).save(any());
        }

        @Test
        void shouldCreateCategoryWhenNotFound() {
            when(productRepository.existsByNameAndBrand("iPhone", "Apple")).thenReturn(false);
            when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            when(productMapper.toProduct(addRequest)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            ProductDto result = productService.createProduct(addRequest);

            assertNotNull(result);
            verify(categoryRepository).findByName("Electronics");
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Get Product Tests")
    class GetProductTests {

        @Test
        void shouldGetProductById() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            ProductDto result = productService.getProductById(productId);

            assertNotNull(result);
            assertEquals(productId, result.getId());
            verify(productRepository).findById(productId);
        }

        @Test
        void shouldThrowWhenGetProductByIdNotFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
            verify(productRepository).findById(productId);
        }

        @Test
        void shouldGetAllProductsPaginated() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Product> page = new PageImpl<>(List.of(product));
            when(productRepository.findAll(pageable)).thenReturn(page);
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            PageResponse<ProductDto> result = productService.getAllProducts(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("iPhone", result.getContent().getFirst().getName());
            verify(productRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Update and Delete Product Tests")
    class UpdateDeleteProductTests {

        @Test
        void shouldUpdateProduct() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
            when(categoryRepository.findByName(any())).thenReturn(Optional.empty());
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            ProductDto result = productService.updateProduct(updateRequest, productId);

            assertNotNull(result);
            verify(productRepository).findById(productId);
            verify(productRepository).save(product);
        }

        @Test
        void shouldDeleteProductById() {
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            doNothing().when(productRepository).delete(product);

            productService.deleteProductById(productId);

            verify(productRepository).findById(productId);
            verify(productRepository).delete(product);
        }

        @Test
        void shouldThrowWhenDeleteProductNotFound() {
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(productId));
            verify(productRepository).findById(productId);
            verify(productRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Search Product Tests")
    class SearchProductTests {

        @Test
        void shouldGetProductsByCategory() {
            when(productRepository.findByCategoryName("Electronics")).thenReturn(List.of(product));
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            ProductListResponse result = productService.getProductsByCategory("Electronics");

            assertEquals(1, result.getProducts().size());
            verify(productRepository).findByCategoryName("Electronics");
        }

        @Test
        void shouldGetProductsByBrand() {
            when(productRepository.findByBrand("Apple")).thenReturn(List.of(product));
            when(productMapper.ProductToProductDto(product)).thenReturn(productDto);

            ProductListResponse result = productService.getProductsByBrand("Apple");

            assertEquals(1, result.getProducts().size());
            verify(productRepository).findByBrand("Apple");
        }
    }
}
