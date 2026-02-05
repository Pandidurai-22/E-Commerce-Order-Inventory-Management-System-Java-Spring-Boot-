package com.example.Ecommerce.repository;

import com.example.Ecommerce.model.Order;
import com.example.Ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUser(User user);
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);
    List<Order> findByUserAndStatus(User user, String status);
}