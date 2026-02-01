package com.bidding.platform.buyer.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class BuyerBidPageDto {
    // --- Header Section ---
    private Long reqId;
    private String category;      // "Electronic"
    private String status;        // "Active"
    private String productName;   // "Industrial LED Lights"
    private String quantity;      // "1200 Units"
    
    // Dynamic Specs for Header (Brand, Warranty, etc.)
    private Map<String, String> specs; 

    // --- Table Section ---
    private List<BidRowDto> bids;

    @Data
    public static class BidRowDto {
        private Long bidId;
        private String sellerName;    // "PrimePack Logistics"
        private Double bidAmount;     // 18200.0
        private String moq;           // "900 pcs"
        private String offeredQty;    // "1200 pcs"
        private String deliveryTime;  // "6-8 days"
        private LocalDate updatedAt;  // "14/02/2025"
    }
}
