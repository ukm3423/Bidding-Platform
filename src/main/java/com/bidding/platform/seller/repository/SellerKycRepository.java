package com.bidding.platform.seller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.platform.auth.models.SellerKyc;

public interface SellerKycRepository extends JpaRepository<SellerKyc, Long> {

    SellerKyc findByUserId(Long userId);
}
