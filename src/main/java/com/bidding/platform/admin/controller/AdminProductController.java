package com.bidding.platform.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.admin.dto.ProductCreateRequest;
import com.bidding.platform.admin.dto.ProductParameterDto;
import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProductController {
	
	@Autowired
	private ProductService productService;
	// For Creation of Product 30/12/2025
	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody ProductCreateRequest request){
		return ResponseEntity.ok(productService.createProduct(request));
	}
	
	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts(){
		return ResponseEntity.ok(productService.getAllProducts());
	}
	
	// For Addition of Params in Products
    @PostMapping("/{id}/parameters")
    public ResponseEntity<Product> addProductParameters(
            @PathVariable Long id, 
            @RequestBody List<ProductParameterDto> parameters) {
        
        System.out.println("Adding parameters to Product ID: " + id);
        return ResponseEntity.ok(productService.addParameters(id, parameters));
    }

}
