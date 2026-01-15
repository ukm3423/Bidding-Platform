package com.bidding.platform.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDTO {

	private Long id;
	private String name;
    private String description;

    // FULL MinIO URL
    private String imageUrl;
}
