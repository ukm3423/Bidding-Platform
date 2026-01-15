package com.bidding.platform.buyer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.buyer.dto.RequirementPostRequest;
import com.bidding.platform.buyer.model.Requirements;
import com.bidding.platform.buyer.service.RequirementService;
import com.bidding.platform.seller.model.Bid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/buyer/requirements")
@RequiredArgsConstructor
public class BuyerRequirementController {

	@Autowired
    private RequirementService requirementService;
	@Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Requirements> postRequirement(
            @RequestBody RequirementPostRequest request,
            Authentication authentication // Get logged in user
    ) {
        // For now, if you haven't set up full JWT SecurityContext, 
        // you might need to pass buyerId in the URL or Body for testing.
        // Assuming Security is working:
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        return ResponseEntity.ok(requirementService.postRequirement(user.getId(), request));
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
}
