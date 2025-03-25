package com.khalaarte.ecommerce.dto;

import com.khalaarte.ecommerce.model.Cart;
import com.khalaarte.ecommerce.model.CartItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private UserDTO user;
    private List<CartItem> cartItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartDTO(Cart cart) {
        this.id = cart.getId();
        this.user = new UserDTO(cart.getUser());
        this.cartItems = cart.getCartItems();
        this.createdAt = cart.getCreatedAt();
        this.updatedAt = cart.getUpdatedAt();
    }
}
