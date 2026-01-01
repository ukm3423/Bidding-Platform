package com.bidding.platform.modules.payment.dto;

import lombok.Data;

@Data
public class RazorpayVerifyDto {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}
