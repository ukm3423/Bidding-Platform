package com.bidding.platform.buyer.dto;

import java.util.Map;

import lombok.Data;

@Data
public class RequirementPostRequest {
    private Long productId;
    
 // Step 1
    private Double quantity;
    private String quantityUnit;
    private boolean splitOrderAllowed;
    private Double minQuantity;
    private String rejectionClause;
    private String additionalDetails;
    
    // Step 2
    private String deliveryUnitName;
    private String deliveryStreet;
    private String deliveryLocality;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPincode;
    private Integer deliveryPeriodDays;
    
    private Double budgetAmount;
    private String budgetType;
    private String paymentTerms;
    private String shippingPreference;

    // Dynamic Params (Key: ParamID, Value: "55%")
    private Map<Long, String> dynamicValues;
}
