package com.example.shoppingcartapi.service.cart;

import com.example.shoppingcartapi.dto.ProductDto;
import com.example.shoppingcartapi.exception.ResourceNotFoundException;
import com.example.shoppingcartapi.mapper.ProductMapper;
import com.example.shoppingcartapi.entity.Cart;
import com.example.shoppingcartapi.entity.CartItem;
import com.example.shoppingcartapi.repository.CartItemRepository;
import com.example.shoppingcartapi.repository.CartRepository;
import com.example.shoppingcartapi.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService implements ICartItemService{

    private final CartItemRepository cartItemRepository;
    private final IProductService productService;
    private final ICartService cartService;
    private final CartRepository cartRepository;
    private final ProductMapper productMapper;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        //1. Get the cart
        //2. Get the product
        //3. Check if product already exist in cart
        //4. If yes increase the product quantity
        //5. If no init the new cartItem entry

        Cart cart = cartService.getCart(cartId);
        ProductDto product = productService.getProductById(productId);

        // Check if item exists
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
//            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });

        cart.updateTotalAmount();
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("item not found!"));
    }
}
