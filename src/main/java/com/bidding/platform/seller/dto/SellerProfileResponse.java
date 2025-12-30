package com.bidding.platform.seller.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerProfileResponse {

    // Profile Info
    private String companyName;
    private String email;
    private String gstNumber;
    private String verificationStatus;

    // Statistics
    private int totalBids;
    private int awardedBids;
    private int pendingBids;
    private double winRate;

    // Documents
    private List<SellerDocumentDto> documents;
}
