package com.bidding.platform.auth.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bidding.platform.auth.models.EmailOtp;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.EmailOtpRepository;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.common.dto.ErrorCode;
import com.bidding.platform.common.exceptions.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpService {

    @Autowired
    private EmailOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Value("${otp.fallback.enabled:false}")
    private boolean fallbackEnabled;

    @Value("${otp.fallback.value:246810}")
    private String fallbackOtp;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${otp.rate-limit-seconds:60}")
    private int rateLimitSeconds;

    /**
     * Send OTP to user email with enterprise validations.
     */
    public void sendOtp(User user) {

        log.info("Sending OTP to user: {}", user.getEmail());

        // 1️⃣ Rate-Limit Check
        validateRateLimit(user);

        // 2️⃣ Invalidate last OTP
        otpRepository.findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(user)
            .ifPresent(oldOtp -> {
                oldOtp.setUsed(true);
                otpRepository.save(oldOtp);
                log.debug("Previous OTP invalidated for {}", user.getEmail());
            });

        // 3️⃣ Generate new OTP
        String otpCode = generateOtp();

        EmailOtp emailOtp = EmailOtp.builder()
                .user(user)
                .otpCode(otpCode)
                .isUsed(false)
                .attemptCount(0L)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepository.save(emailOtp);

        // 4️⃣ Send OTP via Email
//        emailService.sendOtpEmail(user.getEmail(), otpCode);

        log.info("OTP generated & sent to {}", user.getEmail());
    }

    /**
     * Verify OTP
     */
    public boolean verifyOtp(String email, String otp) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_REGISTERED, "User not found"));

        EmailOtp emailOtp = otpRepository.findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OTP, "OTP not found or expired"));

        // 1️⃣ Fallback OTP for developers/testing
        if (fallbackEnabled && otp.equals(fallbackOtp)) {
            log.warn("Fallback OTP used for {}", email);
            markOtpUsed(emailOtp);
            return true;
        }

        // 2️⃣ Check expiration
        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.OTP_EXPIRED, "OTP has expired");
        }

        // 3️⃣ Check attempt limit
        if (emailOtp.getAttemptCount() >= maxAttempts) {
            throw new BusinessException(ErrorCode.OTP_ATTEMPTS_EXCEEDED,
                    "Maximum OTP attempts exceeded");
        }

        // 4️⃣ Check OTP correctness
        if (!emailOtp.getOtpCode().equals(otp)) {
            // Increase attempt count
            emailOtp.setAttemptCount(emailOtp.getAttemptCount() + 1);
            otpRepository.save(emailOtp);

            throw new BusinessException(ErrorCode.INVALID_OTP, "Invalid OTP");
        }

        // 5️⃣ OTP is valid → mark used
        markOtpUsed(emailOtp);

        log.info("OTP verified successfully for {}", email);

        return true;
    }

    /**
     * Validate rate limit - prevents spam OTP requests
     */
    private void validateRateLimit(User user) {
        otpRepository.findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(user)
                .ifPresent(lastOtp -> {
                    if (lastOtp.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(rateLimitSeconds))) {
                        throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED,
                                "Please wait before requesting another OTP");
                    }
                });
    }

    /**
     * Invalidate and mark OTP as used
     */
    private void markOtpUsed(EmailOtp otpEntity) {
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
    }

    /**
     * Generate 6-digit OTP
     */
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }	


}
