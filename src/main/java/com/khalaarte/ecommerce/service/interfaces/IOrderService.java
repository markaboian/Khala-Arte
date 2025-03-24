package com.khalaarte.ecommerce.service.interfaces;

import com.khalaarte.ecommerce.model.Order;

import java.util.List;
import java.util.Optional;

public interface IOrderService {

    Optional<Order> getOrderById(Long id);

    List<Order> getAllOrders();

    Order createOrder(Long userId, List<Long> productIds);
    Order updateOrder(Order order);

    void deleteOrderById(Long id);
}
