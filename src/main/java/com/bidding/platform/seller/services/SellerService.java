package com.bidding.platform.seller.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bidding.platform.admin.model.Product;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.buyer.model.Requirements;
import com.bidding.platform.buyer.repository.RequirementRepository;
import com.bidding.platform.seller.dto.BidPlaceRequest;
import com.bidding.platform.seller.dto.BidStatsDto;
import com.bidding.platform.seller.dto.SellerBidResponseDto;
import com.bidding.platform.seller.dto.SellerRequirementFeedDto;
import com.bidding.platform.seller.model.Bid;
import com.bidding.platform.seller.repository.BidRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final RequirementRepository requirementsRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public List<SellerRequirementFeedDto> getMarketplaceFeed(Long sellerId) {
        // 1. Fetch all OPEN requirements
        List<Requirements> openReqs = requirementsRepository.findByStatusOrderByCreatedAtDesc("OPEN");

        // 2. Convert to DTOs
        return openReqs.stream()
                .map(req -> mapToFeedDto(req, sellerId))
                .collect(Collectors.toList());
    }

    private SellerRequirementFeedDto mapToFeedDto(Requirements req, Long sellerId) {
        SellerRequirementFeedDto dto = new SellerRequirementFeedDto();
        
        dto.setId(req.getId());
        dto.setReqId("REQ" + String.format("%04d", req.getId()));

        // --- CORRECT MAPPING LOGIC ---
        Product product = req.getProduct();
        
        // 1. Title comes from Product Name
        if (product != null) {
            dto.setTitle(product.getName());
            
            // 2. Category comes from Product -> Category
            if (product.getCategory() != null) {
                dto.setProductCategory(product.getCategory().getName());
            } else {
                dto.setProductCategory("General");
            }
        } else {
            dto.setTitle("Unknown Product");
            dto.setProductCategory("General");
        }

        dto.setDescription(req.getAdditionalDetails());

        // Formatting Budget
        dto.setBudget(req.getBudgetAmount() != null ? "₹" + req.getBudgetAmount() : "Not Disclosed");

        // Formatting Location
        String city = req.getDeliveryCity() != null ? req.getDeliveryCity() : "";
        String state = req.getDeliveryState() != null ? req.getDeliveryState() : "";
        dto.setLocation(city + (city.isEmpty() || state.isEmpty() ? "" : ", ") + state);

        // Calculating Deadline
        int days = req.getDeliveryPeriodDays() != null ? req.getDeliveryPeriodDays() : 7;
        if (req.getCreatedAt() != null) {
            dto.setDeadline(req.getCreatedAt().toLocalDate().plusDays(days));
        } else {
            dto.setDeadline(LocalDate.now().plusDays(days));
        }

        dto.setQuantity(req.getQuantity() != null ? req.getQuantity().intValue() : 0);
        dto.setUnit(req.getQuantityUnit());

        // Check if already bidded
        boolean alreadyBidded = false;
        if (req.getBids() != null) {
            alreadyBidded = req.getBids().stream()
                    .anyMatch(bid -> bid.getSeller().getId().equals(sellerId));
        }
        dto.setAlreadyBidded(alreadyBidded);

        // Map Specifications (RequirementValue -> DTO Map)
        Map<String, String> specs = new HashMap<>();
        if (req.getValues() != null) {
            req.getValues().forEach(val -> {
                if (val.getParameter() != null) {
                    specs.put(val.getParameter().getParamName(), val.getValue());
                }
            });
        }
        dto.setSpecifications(specs);

        return dto;
    }
    
    
 // NEW METHOD: Place a Bid
    public Bid placeBid(Long sellerId, BidPlaceRequest request) {
        // 1. Validate Seller (User)
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // 2. Validate Requirement
        Requirements requirement = requirementsRepository.findById(request.getRequirementId())
                .orElseThrow(() -> new RuntimeException("Requirement not found"));

        if (!"OPEN".equals(requirement.getStatus())) {
            throw new RuntimeException("This requirement is no longer open for bidding.");
        }

        // 3. Create Bid Entity
        Bid bid = new Bid();
        bid.setSeller(seller);
        bid.setRequirement(requirement);
        bid.setBidAmount(request.getBidAmount());
        bid.setDeliveryTime(request.getDeliveryTime());
        bid.setMoq(request.getMoq());
        bid.setOfferedQty(request.getOfferedQty());
        bid.setStatus("PENDING"); // Default status

        // 4. Save
        return bidRepository.save(bid);
    }
    
 // Get All My Bids (Mapped to DTO)
    public List<SellerBidResponseDto> getMyBids(Long sellerId) {
        List<Bid> bids = bidRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        return bids.stream().map(this::mapToBidDto).collect(Collectors.toList());
    }
    
 //  Get Bid Stats
    public BidStatsDto getBidStats(Long sellerId) {
        List<Bid> bids = bidRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        
        BidStatsDto stats = new BidStatsDto();
        stats.setTotalBids(bids.size());
        stats.setPending(bids.stream().filter(b -> "PENDING".equalsIgnoreCase(b.getStatus())).count());
        stats.setShortlisted(bids.stream().filter(b -> "SHORTLISTED".equalsIgnoreCase(b.getStatus())).count());
        stats.setAwarded(bids.stream().filter(b -> "AWARDED".equalsIgnoreCase(b.getStatus())).count());
        stats.setRejected(bids.stream().filter(b -> "REJECTED".equalsIgnoreCase(b.getStatus())).count());
        
        return stats;
    }
    
 // Helper: Map Entity -> DTO
    private SellerBidResponseDto mapToBidDto(Bid bid) {
        SellerBidResponseDto dto = new SellerBidResponseDto();
        
        dto.setBidId(bid.getId());
        dto.setBidStatus(bid.getStatus());
        dto.setBidAmount(bid.getBidAmount());
        dto.setDeliveryTime(bid.getDeliveryTime());
        dto.setSubmittedDate(bid.getCreatedAt().toLocalDate());
        
        // EMD Placeholder (Logic can be added later)
        dto.setEmdAmount("₹50,000"); 

        if (bid.getRequirement() != null) {
            dto.setReqId(bid.getRequirement().getId());
            dto.setReqDisplayId("REQ" + String.format("%04d", bid.getRequirement().getId()));
            
            // Product Name as Title
            if (bid.getRequirement().getProduct() != null) {
                dto.setTitle(bid.getRequirement().getProduct().getName());
            } else {
                dto.setTitle("Unknown Product");
            }

            // Buyer Name
            if (bid.getRequirement().getBuyer() != null) {
                dto.setBuyerName(bid.getRequirement().getBuyer().getFullname());
            } else {
                dto.setBuyerName("Unknown Buyer");
            }
        }
        
        // Set specific status messages based on status
        if ("SHORTLISTED".equalsIgnoreCase(bid.getStatus())) {
            dto.setStatusMessage("Your bid has been shortlisted. The buyer is reviewing final submissions.");
        } else if ("AWARDED".equalsIgnoreCase(bid.getStatus())) {
            dto.setStatusMessage("Congratulations! Your bid has been awarded. The buyer will contact you soon.");
        } else if ("REJECTED".equalsIgnoreCase(bid.getStatus())) {
            dto.setStatusMessage("Your bid was not selected. Better luck next time.");
        } else {
            dto.setStatusMessage("Your bid is under review.");
        }

        return dto;
    }
    
}