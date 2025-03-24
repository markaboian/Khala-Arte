package com.khalaarte.ecommerce.controller;

import com.khalaarte.ecommerce.model.Order;
import com.khalaarte.ecommerce.model.OrderDetail;
import com.khalaarte.ecommerce.model.Product;
import com.khalaarte.ecommerce.service.implementations.OrderService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return orders.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order newOrder = orderService.createOrder(orderRequest.getUserId(), orderRequest.getProductIds());
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody OrderUpdateRequest orderUpdateRequest) {
        if (!id.equals(orderUpdateRequest.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Order updateOrder = orderService.updateOrder(orderUpdateRequest.toOrder());
        return ResponseEntity.ok(updateOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return new ResponseEntity<>("Order with the id of " + id + " deleted successfully.", HttpStatus.ACCEPTED);
    }

}

class OrderRequest {
    @Getter
    @Setter
    private Long userId;

    @Getter
    @Setter
    private List<Long> productIds;
}

class OrderUpdateRequest {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private List<Long> productIds;

    public Order toOrder() {
        Order order = new Order();
        order.setId(this.id);
        order.setStatus(this.status);

        if (this.productIds != null && !this.productIds.isEmpty()) {
            List<OrderDetail> orderDetails = new ArrayList<>();

            for (Long productId : this.productIds) {
                OrderDetail detail = new OrderDetail();
                Product product = new Product();

                product.setId(productId);

                detail.setProduct(product);

                orderDetails.add(detail);
            }
            order.setOrderDetails(orderDetails);
        }
        return order;
    }
}
