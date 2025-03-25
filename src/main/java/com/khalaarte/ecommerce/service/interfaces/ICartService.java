package com.khalaarte.ecommerce.service.interfaces;

import com.khalaarte.ecommerce.model.Cart;

import java.util.List;
import java.util.Optional;

public interface ICartService {

    Optional<Cart> getCartById(Long id);
    Optional<Cart> findByUserId(Long userId);

    List<Cart> getAllCarts();

    Cart createCart(Cart cart);
    Cart updateCart(Cart cart);

    void deleteCartById(Long id);
}
