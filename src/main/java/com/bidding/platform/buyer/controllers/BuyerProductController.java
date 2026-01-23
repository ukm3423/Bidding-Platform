package com.bidding.platform.buyer.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.admin.dto.CategoryResponseDTO;
import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.repository.CategoryRepository;
import com.bidding.platform.admin.repository.ProductParameterRepository;
import com.bidding.platform.admin.repository.ProductRepository;
import com.bidding.platform.admin.services.ProductService;
import com.bidding.platform.buyer.dto.ProductParameterResponse;
import com.bidding.platform.common.dto.ErrorCode;
import com.bidding.platform.common.exceptions.BusinessException;
import com.bidding.platform.common.services.MinioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerProductController {
	
	@Autowired
    private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
    private ProductService productService;
	
	@Autowired
	private MinioService minioService;

    // 1. Get All Categories (For the dashboard cards)    
    @GetMapping("/categories")
	public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
		return ResponseEntity.ok(productService.getAllCategories());
	}
	

    // 2. Get Products by Category (When clicking "Browse Products")
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam Long categoryId) {
        
        // FIX: Unwrap the Optional. 
        // We check if the category exists. If not, return 404 (Not Found).
        Category category = categoryRepository.findById(categoryId)
        		.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND,"Unknown Category"));
        
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        List<Product> products = productRepository.findByCategory(category);

        for (Product product : products) {
        	String imageURL = minioService.generatePresignedUrl(product.getProductUrl());
        	product.setProductUrl(imageURL);
		}
        
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/{productId}/parameters")
    public ResponseEntity<List<ProductParameterResponse>> getProductParameters(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
        		productService.getParameterByProductId(productId)
        );
    }


}