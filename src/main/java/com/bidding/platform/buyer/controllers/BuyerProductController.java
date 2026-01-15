package com.bidding.platform.buyer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.repository.CategoryRepository;
import com.bidding.platform.admin.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerProductController {
	
	@Autowired
    private CategoryRepository categoryRepository;
    @Autowired
	private ProductRepository productRepository;

    // 1. Get All Categories (For the dashboard cards)
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    // 2. Get Products by Category (When clicking "Browse Products")
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam Long categoryId) {
        // Assuming your Product entity has a 'category' field
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }
}