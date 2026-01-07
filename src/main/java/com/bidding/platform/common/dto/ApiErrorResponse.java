package com.bidding.platform.common.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private int status;
    private ErrorCode errorCode;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
