package com.bidding.platform.auth.services;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bidding.platform.auth.models.EmailOtp;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.EmailOtpRepository;
import com.bidding.platform.auth.repository.UserRepository;

@Service
public class OtpService {

    @Autowired
    private EmailOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Send OTP to user email
     */
    public void sendOtp(User user) {

        // 1. Invalidate previous OTPs
        otpRepository.findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(user)
                .ifPresent(oldOtp -> {
                    oldOtp.setUsed(true);
                    otpRepository.save(oldOtp);
                });

        // 2. Generate 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // 3. Create OTP entity
        EmailOtp emailOtp = EmailOtp.builder()
                .user(user)
                .otpCode(otp)
                .isUsed(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // OTP valid for 5 min
                .build();

        otpRepository.save(emailOtp);

        // 4. Send OTP via email
//        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    /**
     * Verify OTP
     */
    public boolean verifyOtp(String email, String otp) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmailOtp emailOtp = otpRepository
                .findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (emailOtp.isUsed()) return false;
        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!emailOtp.getOtpCode().equals(otp)) return false;

        emailOtp.setUsed(true);
        otpRepository.save(emailOtp);

        return true;
    }
}
