package com.example.Ecommerce.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderItemRequestDTO{
    private Long productId;
    private Integer quantity;
}