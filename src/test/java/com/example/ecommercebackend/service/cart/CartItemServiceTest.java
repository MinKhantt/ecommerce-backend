package com.example.ecommercebackend.service.cart;

import com.example.ecommercebackend.dto.CartItemDto;
import com.example.ecommercebackend.entity.Cart;
import com.example.ecommercebackend.entity.CartItem;
import com.example.ecommercebackend.entity.Product;
import com.example.ecommercebackend.entity.User;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.mapper.CartItemMapper;
import com.example.ecommercebackend.repository.CartRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import com.example.ecommercebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemService Unit Test")
class CartItemServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private CartItemDto cartItemDto;
    private UUID userId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        product = new Product("Test Product", "TestBrand", BigDecimal.TEN, 100, "desc", null);
        product.setId(productId);

        cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());

        cartItem = new CartItem();
        cartItem.setId(UUID.randomUUID());
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(BigDecimal.TEN);

        cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setQuantity(2);
        cartItemDto.setUnitPrice(BigDecimal.TEN);
    }

    @Nested
    @DisplayName("Add Item to Cart Tests")
    class AddItemToCartTests {

        @Test
        void shouldAddItemToExistingCart() {
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(cartItemMapper.ToCartItemDto(any(CartItem.class))).thenReturn(cartItemDto);

            CartItemDto result = cartItemService.addItemToCart(productId, 2, userId);

            assertNotNull(result);
            verify(cartRepository).findByUserId(userId);
            verify(productRepository).findById(productId);
            verify(cartItemMapper).ToCartItemDto(any(CartItem.class));
        }

        @Test
        void shouldCreateNewCartWhenNotFound() {
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));
            when(cartItemMapper.ToCartItemDto(any(CartItem.class))).thenReturn(cartItemDto);

            CartItemDto result = cartItemService.addItemToCart(productId, 2, userId);

            assertNotNull(result);
            verify(cartRepository).findByUserId(userId);
            verify(userRepository).findById(userId);
            verify(cartRepository).save(any(Cart.class));
            verify(productRepository).findById(productId);
        }

        @Test
        void shouldThrowWhenProductNotFound() {
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> cartItemService.addItemToCart(productId, 2, userId));
            verify(productRepository).findById(productId);
        }
    }

    @Nested
    @DisplayName("Update and Remove Cart Item Tests")
    class UpdateRemoveCartItemTests {

        @Test
        void shouldUpdateItemQuantity() {
            cart.getItems().add(cartItem);
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

            cartItemService.updateItemQuantity(productId, 5, userId);

            verify(cartRepository).findByUserId(userId);
        }

        @Test
        void shouldRemoveItemFromCart() {
            cart.getItems().add(cartItem);
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

            cartItemService.removeItemFromCart(productId, userId);

            verify(cartRepository).findByUserId(userId);
            assertTrue(cart.getItems().isEmpty());
        }

        @Test
        void shouldThrowWhenRemoveFromCartNotFound() {
            when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> cartItemService.removeItemFromCart(productId, userId));
            verify(cartRepository).findByUserId(userId);
        }
    }
}
