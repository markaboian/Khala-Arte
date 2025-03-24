package com.khalaarte.ecommerce.service.implementations;

import com.khalaarte.ecommerce.model.Order;
import com.khalaarte.ecommerce.model.OrderDetail;
import com.khalaarte.ecommerce.model.Product;
import com.khalaarte.ecommerce.model.User;
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
        // Find User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        // Create order detail of the product
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found."));

            // Validate the product is not sold
            if (!product.isAvailable()) {
                throw new RuntimeException("The product " + product.getName() + " is already sold.");
            }

            // Create the OrderDetail
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setUnitPrice(product.getPrice());
            orderDetails.add(detail);

            // Set product availability false
            product.setAvailable(false);
        }

        // Assign the details to the order and calculate the total
        order.setOrderDetails(orderDetails);
        order.calculateTotal();

        // Save the order (and the details thanks to the Cascade)
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
}
