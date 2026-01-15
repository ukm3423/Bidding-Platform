package com.bidding.platform.admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private String imageUrl;
    private CategoryResponseDTO category;
    private List<ProductParameterDto> parameters;
}
