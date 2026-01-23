package com.bidding.platform.seller.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SellerBidResponseDto {
    private Long bidId;
    private String bidStatus;      // PENDING, SHORTLISTED, AWARDED, REJECTED
    
    // Requirement Details
    private Long reqId;            // Database ID
    private String reqDisplayId;   // "REQ001"
    private String title;          // Product Name (e.g. "Industrial Steel Pipes")
    private String buyerName;      // "Industrial Corp Ltd"
    
    // Bid Details
    private Double bidAmount;
    private String emdAmount;      // Placeholder for now (e.g. "₹50,000")
    private String deliveryTime;   // "30 Days"
    private LocalDate submittedDate;
    
    // Logic for UI message ("Your bid has been shortlisted...")
    private String statusMessage; 
}
