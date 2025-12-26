package com.bidding.platform.auth.services;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}
