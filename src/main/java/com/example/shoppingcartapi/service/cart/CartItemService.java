package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.dto.CartItemDto;
import com.example.shoppingcartapi.entity.Product;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.mapper.CartItemMapper;
import com.example.shoppingcartapi.entity.Cart;
import com.example.shoppingcartapi.entity.CartItem;
import com.example.shoppingcartapi.repository.CartRepository;
import com.example.shoppingcartapi.repository.ProductRepository;
import com.example.shoppingcartapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService implements ICartItemService{

    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartItemDto addItemToCart(UUID productId, int quantity, UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setUnitPrice(product.getPrice());
                    cart.addItem(newItem);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItem.calculateTotalPrice();
        cart.updateTotalAmount();
        return cartItemMapper.ToCartItemDto(cartItem);
    }

    @Override
    @Transactional
    public void updateItemQuantity(UUID productId, int quantity, UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this cart.");
        }

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                    cart.updateTotalAmount();
                }, () -> {
                    throw new ResourceNotFoundException("Product not found in cart!");
                });
    }


    @Override
    @Transactional
    public void removeItemFromCart(UUID productId, UUID userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this cart.");
        }

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cart.updateTotalAmount();
    }

    @Override
    public CartItemDto getCartItem(UUID cartId, UUID productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found!"));

        return cartItemMapper.ToCartItemDto(item);
    }
}
