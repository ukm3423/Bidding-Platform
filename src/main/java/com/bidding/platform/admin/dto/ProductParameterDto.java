package com.bidding.platform.admin.dto;



import com.bidding.platform.admin.model.DataType;

import lombok.Data;

@Data
public class ProductParameterDto {
    private String paramName;    // e.g., "Moisture"
    private DataType dataType;   
    private boolean isMandatory; // e.g., true

}
