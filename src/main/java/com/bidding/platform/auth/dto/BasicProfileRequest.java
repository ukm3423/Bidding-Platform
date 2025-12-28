package com.bidding.platform.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class BasicProfileRequest {

    private String companyName;

    private String mobileNumber;
    private String alternateMobileNumber;

    private String unitName;
    private String unitType; // FACTORY / DEPOT
    private String street;
    private String locality;
    private String city;
    private String state;
    private String pincode;

    private List<Long> productIds;
}