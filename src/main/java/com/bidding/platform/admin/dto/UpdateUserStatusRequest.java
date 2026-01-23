package com.bidding.platform.admin.dto;

import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    private Long userId;
    private String status; // ACTIVE, REJECTED
}
