package com.bidding.platform.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "seller_kyc")
public class SellerKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String gstNumber;

    private String gstCertificateUrl;
    private String businessCertificateUrl;
    private String turnoverCertificateUrl;

    private String kycStatus; // SUBMITTED / APPROVED / REJECTED
}
