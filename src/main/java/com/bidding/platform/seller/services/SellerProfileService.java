package com.bidding.platform.seller.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bidding.platform.auth.models.Company;
import com.bidding.platform.auth.models.SellerKyc;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.common.services.MinioService;
import com.bidding.platform.seller.dto.SellerDocumentDto;
import com.bidding.platform.seller.dto.SellerProfileResponse;
import com.bidding.platform.seller.repository.SellerKycRepository;
import com.bidding.platform.users.repo.CompanyRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class SellerProfileService {

    private final UserRepository sellerRepository;
    private final SellerKycRepository documentRepository;
    private final CompanyRepository companyRepository;
    private final MinioService minioService;

    public SellerProfileResponse getSellerProfile(String email) {

        User seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        int totalBids = 180;
        int awarded = 101;
        int pending = 79;

        double winRate = totalBids == 0 ? 0 : (awarded * 100.0) / totalBids;

        SellerKyc sellerKyc = documentRepository.findByUserId(seller.getId());
        Company company = companyRepository.findByUserId(seller.getId());

        List<SellerDocumentDto> sellerDocList = new ArrayList<>();

        sellerDocList.add(
            SellerDocumentDto.builder()
                .documentName("Business Certificate")
                .documentUrl(minioService.generatePresignedUrl(sellerKyc.getBusinessCertificateUrl()))
                .status(sellerKyc.getKycStatus())
                .build()
        );

        sellerDocList.add(
            SellerDocumentDto.builder()
                .documentName("GST Certificate")
                .documentUrl(minioService.generatePresignedUrl(
                        sellerKyc.getGstCertificateUrl()
                        ))
                .status(sellerKyc.getKycStatus())
                .build()
        );

        sellerDocList.add(
            SellerDocumentDto.builder()
                .documentName("Turnover Certificate")
                .documentUrl(minioService.generatePresignedUrl(sellerKyc.getTurnoverCertificateUrl()))
                .status(sellerKyc.getKycStatus())
                .build()
        );

        return new SellerProfileResponse(
                company.getCompanyName(),
                seller.getEmail(),
                sellerKyc.getGstNumber(),
                seller.getStatus(), 
                totalBids,
                awarded,
                pending,
                winRate,
                sellerDocList
        );
    }
}

