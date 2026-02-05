package com.example.Ecommerce.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO{
    private Long id;
    private String status;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private List<OrderItemResponseDTO> items;
    private Double total;
}