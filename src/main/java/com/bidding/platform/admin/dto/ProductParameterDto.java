package com.bidding.platform.admin.dto;



import com.bidding.platform.admin.model.DataType;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductParameterDto {

    @NotBlank(message = "Parameter name cannot be empty")
    @Size(min = 2, max = 100, message = "Parameter name must be between 2 and 100 characters")
    private String paramName;

    @NotNull(message = "Data type is required")
    private DataType dataType;

    private Boolean isMandatory;
    
    private String unit;
}
