package com.bidding.platform.buyer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.buyer.dto.BuyerBidPageDto;
import com.bidding.platform.buyer.dto.RequirementPostRequest;
import com.bidding.platform.buyer.model.Requirements;
import com.bidding.platform.buyer.service.RequirementService;
import com.bidding.platform.common.dto.ErrorCode;
import com.bidding.platform.common.exceptions.BusinessException;
import com.bidding.platform.seller.model.Bid;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/buyer/requirements")
@RequiredArgsConstructor
public class BuyerRequirementController {

	private final RequirementService requirementService;
    private final UserRepository userRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Requirements> postRequirement(
            @Valid @RequestPart("data") RequirementPostRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @Valid Authentication authentication
    ) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,"USER_NOT_FOUND"));

        Requirements savedRequirement =
                requirementService.postRequirement(user.getId(), request, files);

        return ResponseEntity.ok(savedRequirement);
    }
    
 // GET /api/buyer/requirements/my-posts
    @GetMapping("/my-posts")
    public ResponseEntity<List<Requirements>> getMyRequirements(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(requirementService.getBuyerRequirements(user.getId()));
    }

    // GET /api/buyer/requirements/{id}/bids
    @GetMapping("/{id}/bids")
    public ResponseEntity<List<Bid>> getViewBids(@PathVariable Long id) {
        return ResponseEntity.ok(requirementService.getBidsForRequirement(id));
    }
    
    @GetMapping("/{id}/bids-view")
    public ResponseEntity<BuyerBidPageDto> getRequirementWithBids(@PathVariable Long id) {
        return ResponseEntity.ok(requirementService.getRequirementWithBids(id));
    }
}
