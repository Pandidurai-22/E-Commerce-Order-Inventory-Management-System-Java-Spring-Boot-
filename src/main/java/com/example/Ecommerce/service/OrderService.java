package com.example.Ecommerce.service;

import com.example.Ecommerce.dto.OrderItemRequestDTO;
import com.example.Ecommerce.dto.OrderItemResponseDTO;
import com.example.Ecommerce.dto.OrderRequestDTO;
import com.example.Ecommerce.dto.OrderResponseDTO;
import com.example.Ecommerce.model.*;
import com.example.Ecommerce.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class OrderService{
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,OrderItemRepository orderItemRepository, ProductRepository productRepository, UserRepository userRepository){
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(Long userId, OrderRequestDTO request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDTO itemRequest : request.getItems()){
            Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"+itemRequest.getProductId()));
            
            if (product.getStock() < itemRequest.getQuantity()){
                throw new RuntimeException("Insufficient stock for product"+ product.getName() + ".Available: "+ product.getStock() + ", Requested: " + itemRequest.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);


            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        Order order =  new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setItems(orderItems);

        for (OrderItem item : orderItems){
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);
        return convertToResponseDTO(savedOrder);
    }

    public OrderResponseDTO getOrderById(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("order not found"));
        return convertToResponseDTO(order);

    }

    public List<OrderResponseDTO> getUserOrders(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, String status){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return convertToResponseDTO(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("order not found"));

        for(OrderItem item : order.getItems()){
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    private OrderResponseDTO convertToResponseDTO(Order order){
        OrderResponseDTO  dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());

        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
            .map(item ->{
                OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                itemDTO.setId(item.getId());
                itemDTO.setProductId(item.getProduct().getId());
                itemDTO.setProductName(item.getProduct().getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setSubtotal(item.getPrice()*item.getQuantity());
                return itemDTO;
            })
            .collect(Collectors.toList());

        dto.setItems(itemDTOs);
        dto.setTotal(itemDTOs.stream()
            .mapToDouble(OrderItemResponseDTO::getSubtotal)
            .sum());

        return dto;

    }

}