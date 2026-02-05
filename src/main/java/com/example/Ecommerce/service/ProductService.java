package com.example.Ecommerce.service;

import com.example.Ecommerce.model.Product;
import com.example.Ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService{
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Product save(Product product){
        return productRepository.save(product);
    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }

    public Product getById(Long id){
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void delete(Long id){
        productRepository.deleteById(id);
    }
    public boolean checkStockAvailability(Long productId, Integer requestQuantity){
        Product product = getById(productId);
        return product.getStock() >= requestQuantity;
    }
}