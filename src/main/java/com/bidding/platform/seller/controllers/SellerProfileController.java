package com.bidding.platform.seller.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.seller.dto.SellerProfileResponse;
import com.bidding.platform.seller.services.SellerProfileService;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('SELLER')")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    @GetMapping("/profile")
    public ResponseEntity<SellerProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername(); // from JWT
        return ResponseEntity.ok(
                sellerProfileService.getSellerProfile(email)
        );
    }
}
