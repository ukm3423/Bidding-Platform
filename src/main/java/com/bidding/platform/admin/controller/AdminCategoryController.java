package com.bidding.platform.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.admin.dto.CategoryCreateRequest;
import com.bidding.platform.admin.dto.CategoryResponseDTO;
import com.bidding.platform.admin.dto.CategoryUpdateRequest;
import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.services.ProductService;
import com.bidding.platform.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@CrossOrigin
public class AdminCategoryController {

	@Autowired
	private ProductService productService;

	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Category> createCategory(@Valid @ModelAttribute CategoryCreateRequest request) {

		return ResponseEntity.ok(productService.createCategory(request));
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
		return ResponseEntity.ok(productService.getAllCategories());
	}
	
	@PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CategoryResponseDTO> updateCategory(
	        @PathVariable Long id,
	        @ModelAttribute CategoryUpdateRequest request) {

	    CategoryResponseDTO updated = productService.updateCategory(id, request);
	    return ResponseEntity.ok(updated);
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {

	    productService.deleteCategory(id);

	    return ResponseEntity.ok(
	    		new ApiResponse("Category deleted successfully")
	    );
	}



}
