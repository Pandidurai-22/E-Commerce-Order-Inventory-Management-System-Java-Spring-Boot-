package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.OrderRequestDTO;
import com.example.Ecommerce.dto.OrderResponseDTO;
import com.example.Ecommerce.model.User;
import com.example.Ecommerce.repository.UserRepository;
import com.example.Ecommerce.service.OrderService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderService.createOrder(user.getId(), request);
    }

    @GetMapping
    public List<OrderResponseDTO> getOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ADMIN can see all orders, CUSTOMER sees only their orders
        if (user.getRole().equals("ADMIN")) {
            return orderService.getAllOrders();
        } else {
            return orderService.getUserOrders(user.getId());
        }
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderResponseDTO order = orderService.getOrderById(id);

        // CUSTOMER can only see their own orders
        if (user.getRole().equals("CUSTOMER") && !order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied! You can only see your orders");
        }

        return order;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public OrderResponseDTO updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        return orderService.updateOrderStatus(id, status);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderResponseDTO order = orderService.getOrderById(id);

        // CUSTOMER can only cancel their own orders
        if (!order.getUserId().equals(user.getId())) {
            throw new RuntimeException("Access denied! You can only cancel your own orders");
        }

        orderService.cancelOrder(id);
    }
}