package com.bidding.platform.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    // success response without data
    public ApiResponse(String message) {
        this.success = true;
        this.message = message;
    }

    // success response with data
    public ApiResponse(String message, Object data) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    // error response
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }
}
