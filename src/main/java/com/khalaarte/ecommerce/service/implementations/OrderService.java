package com.khalaarte.ecommerce.service.implementations;

import com.khalaarte.ecommerce.model.*;
import com.khalaarte.ecommerce.repository.ICartRepository;
import com.khalaarte.ecommerce.repository.IOrderRepository;
import com.khalaarte.ecommerce.repository.IProductRepository;
import com.khalaarte.ecommerce.repository.IUserRepository;
import com.khalaarte.ecommerce.service.interfaces.IOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICartRepository cartRepository;

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found."));

            if (!product.isAvailable()) {
                throw new RuntimeException("The product " + product.getName() + " is already sold.");
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setUnitPrice(product.getPrice());
            orderDetails.add(detail);

            product.setAvailable(false);
        }

        order.setOrderDetails(orderDetails);
        order.calculateTotal();

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != null && !order.getStatus().isEmpty()) {
            existingOrder.setStatus(order.getStatus());
        }

        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            for (OrderDetail existingDetail : existingOrder.getOrderDetails()) {
                Product product = existingDetail.getProduct();
                product.setAvailable(true);
                productRepository.save(product);
            }

            existingOrder.getOrderDetails().clear();

            List<OrderDetail> newOrderDetails = new ArrayList<>();

            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = productRepository.findById(detail.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found."));

                if (!product.isAvailable()) {
                    throw new RuntimeException("The product " + product.getName() + " is already sold.");
                }

                OrderDetail newDetail = new OrderDetail();
                newDetail.setOrder(existingOrder);
                newDetail.setProduct(product);
                newDetail.setUnitPrice(product.getPrice());

                newOrderDetails.add(newDetail);

                product.setAvailable(false);
                productRepository.save(product);
            }

            existingOrder.setOrderDetails(newOrderDetails);

        }

        existingOrder.calculateTotal();

        return orderRepository.save(existingOrder);

    }

    @Override
    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (!product.isAvailable()) {
                throw new RuntimeException("The product " + product.getName() + " is already sold.");
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setUnitPrice(product.getPrice());

            orderDetails.add(detail);

            product.setAvailable(false);
            productRepository.save(product);
        }

        order.setOrderDetails(orderDetails);
        order.calculateTotal();
        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
}
