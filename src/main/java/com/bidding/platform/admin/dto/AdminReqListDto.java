package com.bidding.platform.admin.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdminReqListDto {
    private Long id;
    private String reqDisplayId;  // "REQ-2024-089"
    private String status;        // "OPEN", "CLOSED"
    
    private String buyerName;
    private String productName;
    private String quantity;      // "100 tons"
    private String targetPrice;   // "₹45,000/ton"
    
    private LocalDate postedDate;
    private int bidCount;
}