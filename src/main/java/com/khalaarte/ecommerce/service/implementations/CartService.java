package com.khalaarte.ecommerce.service.implementations;

import com.khalaarte.ecommerce.model.Cart;
import com.khalaarte.ecommerce.model.CartItem;
import com.khalaarte.ecommerce.model.Product;
import com.khalaarte.ecommerce.model.User;
import com.khalaarte.ecommerce.repository.ICartRepository;
import com.khalaarte.ecommerce.repository.IProductRepository;
import com.khalaarte.ecommerce.repository.IUserRepository;
import com.khalaarte.ecommerce.service.interfaces.ICartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartService {

    @Autowired
    private ICartRepository cartRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    @Transactional
    public Cart createCart(Cart cart) {
        User user = userRepository.findById(cart.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found."));

        Optional<Cart> existingCart = cartRepository.findByUserId(user.getId());
        if (existingCart.isPresent()) {
            throw new RuntimeException("User already has a cart with ID " + existingCart.get().getId());
        }

        cart.setUser(user);

//        if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
//            for (CartItem item : cart.getCartItems()) {
//                Product product = productRepository.findById(item.getProduct().getId())
//                        .orElseThrow(() -> new RuntimeException("Product not found"));
//
//                if (!product.isAvailable()) {
//                    throw new RuntimeException("Product " + product.getName() + " is already sold.");
//                }
//
//                item.setCart(cart);
//            }
//        }
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCart(Cart cart) {
        Cart existingCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if(!existingCart.getUser().getId().equals(cart.getUser().getId())) {
            throw new RuntimeException("Cannot change the user of an existing cart");
        }

        if (cart.getCartItems() != null) {
            existingCart.getCartItems().clear();

            for (CartItem item : cart.getCartItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (!product.isAvailable()) {
                    throw new RuntimeException("Product " + product.getName() + " is already sold.");
                }
                item.setCart(existingCart);
                existingCart.getCartItems().add(item);
            }
        }

        return cartRepository.save(existingCart);
    }

    @Override
    public void deleteCartById(Long id) {
        cartRepository.deleteById(id);
    }
}
