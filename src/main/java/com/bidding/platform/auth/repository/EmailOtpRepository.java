package com.bidding.platform.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.platform.auth.models.EmailOtp;
import com.bidding.platform.auth.models.User;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByUserAndIsUsedFalseOrderByCreatedAtDesc(User user);
}
