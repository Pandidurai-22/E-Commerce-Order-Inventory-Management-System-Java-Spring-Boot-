package com.example.Ecommerce.controller;

import com.example.Ecommerce.dto.LoginRequest;
import com.example.Ecommerce.dto.SignupRequest;
import com.example.Ecommerce.model.User;
import com.example.Ecommerce.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.example.Ecommerce.security.JwtUtil;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")

public class AuthController{

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil){
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    
    public User signup(@RequestBody SignupRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")

    public Map<String, String> login(@RequestBody LoginRequest request){
        User user = authService.login(request);


        String token = jwtUtil.generateToken(
            user.getUsername(),
            user.getRole()
        );

        return Map.of(
            "token", token,
            "role", user.getRole()
        );
    }


    // public User login(@RequestBody LoginRequest request){
    //     return authService.login(request);
    // }
}