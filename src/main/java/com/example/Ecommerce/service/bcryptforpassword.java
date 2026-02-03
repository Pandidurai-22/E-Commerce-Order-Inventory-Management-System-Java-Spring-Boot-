package com.example.Ecommerce.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class bcryptforpassword{
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin123"));
    }
}