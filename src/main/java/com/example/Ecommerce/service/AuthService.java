package com.example.Ecommerce.service;

import com.example.Ecommerce.dto.LoginRequest;
import com.example.Ecommerce.dto.SignupRequest;
import com.example.Ecommerce.model.User;
import com.example.Ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, 
    PasswordEncoder passwordEncoder){
        this.userRepository =  userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public User register(SignupRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("CUSTOMER");
        // user.setRole(Role.valueOf(request.getRole()));


        return userRepository.save(user);
    }

    public User login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}