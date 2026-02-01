package com.bidding.platform.admin.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bidding.platform.admin.dto.AdminReqDetailDto;
import com.bidding.platform.admin.dto.AdminReqDetailDto.AdminBidDto;
import com.bidding.platform.admin.dto.AdminReqListDto;
import com.bidding.platform.admin.dto.AdminReqStatsDto;
import com.bidding.platform.buyer.model.Requirements;
import com.bidding.platform.buyer.repository.RequirementRepository;
import com.bidding.platform.seller.repository.BidRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRequirementService {

    private final RequirementRepository requirementsRepository;
    private final BidRepository bidRepository;

    // 1. Get Stats (Cards)
    public AdminReqStatsDto getStats() {
        AdminReqStatsDto stats = new AdminReqStatsDto();
        
        List<Requirements> all = requirementsRepository.findAll();
        
        stats.setTotalRequirements(all.size());
        stats.setOpenRequirements(all.stream().filter(r -> "OPEN".equalsIgnoreCase(r.getStatus())).count());
        stats.setClosedRequirements(all.stream().filter(r -> "CLOSED".equalsIgnoreCase(r.getStatus())).count());
        stats.setTotalBids(bidRepository.count()); // Total bids across platform
        
        return stats;
    }

    // 2. Get All Requirements (List)
    public List<AdminReqListDto> getAllRequirements() {
        return requirementsRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AdminReqListDto mapToDto(Requirements req) {
        AdminReqListDto dto = new AdminReqListDto();
        
        dto.setId(req.getId());
        // Generate a fancy ID format: REQ-YEAR-ID
        String year = req.getCreatedAt().getYear() + "";
        dto.setReqDisplayId("REQ-" + year + "-" + String.format("%03d", req.getId()));
        
        dto.setStatus(req.getStatus());
        dto.setPostedDate(req.getCreatedAt().toLocalDate());
        
        // Buyer Info
        if (req.getBuyer() != null) {
            dto.setBuyerName(req.getBuyer().getFullname());
        } else {
            dto.setBuyerName("Unknown Buyer");
        }

        // Product Info
        if (req.getProduct() != null) {
            dto.setProductName(req.getProduct().getName());
        }

        // Qty & Price
        dto.setQuantity(req.getQuantity() + " " + (req.getQuantityUnit() != null ? req.getQuantityUnit() : ""));
        
        if (req.getBudgetAmount() != null) {
            dto.setTargetPrice("₹" + req.getBudgetAmount());
        } else {
            dto.setTargetPrice("N/A");
        }

        // Bid Count
        dto.setBidCount(req.getBids() != null ? req.getBids().size() : 0);

        return dto;
    }
    
    public AdminReqDetailDto getRequirementDetails(Long id) {
        Requirements req = requirementsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));

        AdminReqDetailDto dto = new AdminReqDetailDto();
        
        // Basic Info
        dto.setId(req.getId());
        dto.setReqDisplayId("REQ-" + req.getCreatedAt().getYear() + "-" + String.format("%03d", req.getId()));
        dto.setBuyerName(req.getBuyer() != null ? req.getBuyer().getFullname() : "Unknown");
        dto.setProductName(req.getProduct() != null ? req.getProduct().getName() : "Unknown");
        dto.setQuantity(req.getQuantity() + " " + req.getQuantityUnit());
        dto.setTargetPrice(req.getBudgetAmount() != null ? "₹" + req.getBudgetAmount() : "N/A");
        dto.setPostedDate(req.getCreatedAt().toLocalDate());
        dto.setClosingDate(req.getCreatedAt().toLocalDate().plusDays(req.getDeliveryPeriodDays() != null ? req.getDeliveryPeriodDays() : 7));

        // Specifications Map
        Map<String, String> specs = new HashMap<>();
        if (req.getValues() != null) {
            req.getValues().forEach(val -> {
                if (val.getParameter() != null) {
                    specs.put(val.getParameter().getParamName(), val.getValue());
                }
            });
        }
        dto.setSpecifications(specs);

        // Bids List
        if (req.getBids() != null) {
            List<AdminBidDto> bidDtos = req.getBids().stream().map(bid -> {
                AdminBidDto b = new AdminBidDto();
                b.setBidId(bid.getId());
                b.setSellerName(bid.getSeller() != null ? bid.getSeller().getFullname() : "Unknown Seller");
                b.setStatus(bid.getStatus());
                b.setBidAmount("₹" + bid.getBidAmount());
                b.setQuantity(bid.getOfferedQty() + " " + req.getQuantityUnit()); // Assuming same unit
                b.setSubmittedDate(bid.getCreatedAt().toLocalDate());
                return b;
            }).collect(Collectors.toList());
            dto.setBids(bidDtos);
        }

        return dto;
    }
}