package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.LoginRequest;
import com.example.Ecommerce.dto.SignupRequest;
import com.example.Ecommerce.model.User;
import com.example.Ecommerce.service.AuthService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")

public class AuthController{

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    
    public User signup(@RequestBody SignupRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")

    public User login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}