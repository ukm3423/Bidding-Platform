package com.bidding.platform.admin.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
	
	@NotBlank(message = "Category name is required")
    private String name;
    private String description;
    private MultipartFile image; // Optional
}