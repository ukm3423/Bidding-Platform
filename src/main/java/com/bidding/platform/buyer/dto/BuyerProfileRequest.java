package com.bidding.platform.buyer.dto;

import java.util.List;

import lombok.Data;

@Data
public class BuyerProfileRequest {

    // ------------------
    // Company Details
    // ------------------
    private String companyName;

    // ------------------
    // Contact Details
    // ------------------
    private String mobileNumber;
    private String alternateMobileNumber;

    // ------------------
    // Delivery Unit Details
    // ------------------
    private String unitName;
    private String unitType;     // FACTORY / DEPOT / WAREHOUSE
    private String street;
    private String locality;
    private String city;
    private String state;
    private String pincode;

    // ------------------
    // Product Interests
    // ------------------
    private List<Long> productIds;
}
