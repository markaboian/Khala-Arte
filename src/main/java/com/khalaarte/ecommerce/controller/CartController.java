package com.khalaarte.ecommerce.controller;

import com.khalaarte.ecommerce.dto.CartDTO;
import com.khalaarte.ecommerce.model.Cart;
import com.khalaarte.ecommerce.model.CartItem;
import com.khalaarte.ecommerce.model.Product;
import com.khalaarte.ecommerce.model.User;
import com.khalaarte.ecommerce.repository.IProductRepository;
import com.khalaarte.ecommerce.repository.IUserRepository;
import com.khalaarte.ecommerce.service.implementations.CartService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long id) {
        return cartService.getCartById(id)
                .map(cart -> ResponseEntity.ok(new CartDTO(cart)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Long userId) {
        return cartService.findByUserId(userId)
                .map(cart -> ResponseEntity.ok(new CartDTO(cart)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<Cart> carts = cartService.getAllCarts();
        List<CartDTO> cartsDTO = carts.stream()
                .map(CartDTO::new)
                .collect(Collectors.toList());
        return carts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(cartsDTO);
    }

    @PostMapping
    public ResponseEntity<CartDTO> createCart(@RequestBody CartRequest cartRequest) {
        Cart cart = new Cart();
        User user = userRepository.findById(cartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found."));
        cart.setUser(user);

        if (cartRequest.getProductIds() != null && !cartRequest.getProductIds().isEmpty()) {
            List<CartItem> cartItems = new ArrayList<>();
            for (Long productId : cartRequest.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (!product.isAvailable()) {
                    throw new RuntimeException("Product " + product.getName() + " is already sold.");
                }

                CartItem item = new CartItem();
                item.setProduct(product);
                item.setCart(cart);

                cartItems.add(item);
            }
            cart.setCartItems(cartItems);
        }
        Cart newCart = cartService.createCart(cart);
        return new ResponseEntity<>(new CartDTO(newCart), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> updateCart(@PathVariable Long id, @RequestBody CartRequest cartRequest) {
        Cart cart = new Cart();
        cart.setId(id);

        Cart updatedCart = cartService.updateCart(cart);
        return new ResponseEntity<>(updatedCart, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartById(@PathVariable Long id) {
        cartService.deleteCartById(id);
        return new ResponseEntity<>("Cart with the id of " + id + " deleted successfully.", HttpStatus.ACCEPTED);
    }

}

@Setter
@Getter
class CartRequest {
    private Long userId;
    private List<Long> productIds;

//    public Cart toCart() {
//        Cart cart = new Cart();
//        User user = new User();
//        user.setId(this.userId);
//        cart.setUser(user);
//
//        if (this.productIds != null && !this.productIds.isEmpty()) {
//            List<CartItem> cartItems = new ArrayList<>();
//            for (Long productId : this.productIds) {
//                CartItem item = new CartItem();
//                Product product = new Product();
//                product.setId(productId);
//                item.setProduct(product);
//                cartItems.add(item);
//            }
//            cart.setCartItems(cartItems);
//        }
//        return cart;
//    }

}