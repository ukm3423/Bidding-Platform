package com.bidding.platform.admin.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AdminReqDetailDto {
    // Top Section
    private Long id;
    private String reqDisplayId; 
    private String buyerName;
    private String productName;
    private String quantity;
    private String targetPrice;
    private LocalDate postedDate;
    private LocalDate closingDate; // Calculated or from DB

    // Middle Section: Specifications (Moisture, Ash, etc.)
    private Map<String, String> specifications;

    // Bottom Section: Bids List
    private List<AdminBidDto> bids;

    @Data
    public static class AdminBidDto {
        private Long bidId;
        private String sellerName;
        private String status;       // PENDING, SHORTLISTED, REJECTED
        private String bidAmount;    // "₹44,500/ton"
        private String quantity;     // "100 tons"
        private LocalDate submittedDate;
    }
}