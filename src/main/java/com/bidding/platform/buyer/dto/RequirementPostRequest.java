package com.bidding.platform.buyer.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class RequirementPostRequest {

    // =============================
    // PRODUCT
    // =============================
    @NotNull(message = "category is required")
    private Long categoryId;
    
    @NotBlank(message = "ProductName is required")
    private String productName;

    // =============================
    // STEP 1: BASIC REQUIREMENT
    // =============================
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @NotBlank(message = "Quantity unit is required")
    private String quantityUnit;

    private boolean splitOrderAllowed;

    @Positive(message = "Minimum quantity must be greater than zero")
    private Double minQuantity;

    @NotBlank(message = "Rejection clause is required")
    private String rejectionClause;

    private String additionalDetails;

    // =============================
    // STEP 2: DELIVERY DETAILS
    // =============================
    @NotBlank(message = "Delivery unit name is required")
    private String deliveryUnitName;

    private String deliveryStreet;
    private String deliveryLocality;

    @NotBlank(message = "Delivery city is required")
    private String deliveryCity;

    @NotBlank(message = "Delivery state is required")
    private String deliveryState;

    @Pattern(
        regexp = "\\d{6}",
        message = "Pincode must be a valid 6 digit number"
    )
    private String deliveryPincode;

    @NotNull(message = "Delivery period is required")
    @Positive(message = "Delivery period must be greater than zero")
    private Integer deliveryPeriodDays;

    // =============================
    // BUDGET & PAYMENT
    // =============================
    @PositiveOrZero(message = "Budget amount cannot be negative")
    private Double budgetAmount;

    @NotBlank(message = "Budget type is required")
    private String budgetType;

    @NotBlank(message = "Payment terms are required")
    private String paymentTerms;

    private String shippingPreference;

    // =============================
    // DYNAMIC PARAMETERS
    // =============================
    private Map<Long, String> dynamicValues;
}
