package com.bidding.platform.admin.dto;



import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductCreateRequest {

    @NotNull(message = "Category ID is required")
    @JsonProperty("categoryId")
    private Long categoryId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;

    
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;
    
    @NotNull(message = "Product image is required")
    private MultipartFile image;
}
