package com.bidding.platform.seller.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.Data;

@Data
public class SellerRequirementFeedDto {
    private Long id;
    private String reqId;          // Display ID: "REQ001"
    private String productCategory;// From Product -> Category
    private String title;          // From Product Name
    private String description;    // From additionalDetails
    
    private String budget;         // "₹15,00,000"
    private String location;       // "Mumbai, Maharashtra"
    private LocalDate deadline;    // Calculated date
    
    private Integer quantity;
    private String unit;

    // e.g. {"Grade": "304", "Diameter": "50mm"}
    private Map<String, String> specifications; 
    
    private boolean isAlreadyBidded;
}