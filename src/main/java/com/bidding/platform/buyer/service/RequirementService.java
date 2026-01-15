package com.bidding.platform.buyer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.model.ProductParameter;
import com.bidding.platform.admin.repository.ProductParameterRepository;
import com.bidding.platform.admin.repository.ProductRepository;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.buyer.dto.RequirementPostRequest;
import com.bidding.platform.buyer.model.RequirementValue;
import com.bidding.platform.buyer.model.Requirements;
import com.bidding.platform.buyer.repository.RequirementRepository;
import com.bidding.platform.buyer.repository.RequirementValueRepository;
import com.bidding.platform.seller.model.Bid;
import com.bidding.platform.seller.repository.BidRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequirementService {
	
	@Autowired
	private RequirementRepository requirementRepository;
	@Autowired
    private ProductRepository productRepository;
    @Autowired
	private UserRepository userRepository;
    @Autowired
    private ProductParameterRepository productParameterRepository;
    @Autowired
    private RequirementValueRepository requirementValueRepository;
    @Autowired
    private BidRepository bidRepository;
	
    @Transactional
    public Requirements postRequirement(Long buyerId, RequirementPostRequest request) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Requirements req = new Requirements();
        req.setBuyer(buyer);
        req.setProduct(product);
        
        // Map Step 1 Fields
        req.setQuantity(request.getQuantity());
        req.setQuantityUnit(request.getQuantityUnit());
        req.setSplitOrderAllowed(request.isSplitOrderAllowed());
        req.setMinQuantity(request.getMinQuantity());
        req.setRejectionClause(request.getRejectionClause());
        req.setAdditionalDetails(request.getAdditionalDetails());

        // Map Step 2 Fields
        req.setDeliveryUnitName(request.getDeliveryUnitName());
        req.setDeliveryStreet(request.getDeliveryStreet());
        req.setDeliveryLocality(request.getDeliveryLocality());
        req.setDeliveryCity(request.getDeliveryCity());
        req.setDeliveryState(request.getDeliveryState());
        req.setDeliveryPincode(request.getDeliveryPincode());
        req.setDeliveryPeriodDays(request.getDeliveryPeriodDays());
        req.setBudgetAmount(request.getBudgetAmount());
        req.setBudgetType(request.getBudgetType());
        req.setPaymentTerms(request.getPaymentTerms());
        req.setShippingPreference(request.getShippingPreference());

        Requirements savedReq = requirementRepository.save(req);

        // Save Dynamic Values (Same as before)
        List<RequirementValue> valuesList = new ArrayList<>();
        if (request.getDynamicValues() != null) {
            request.getDynamicValues().forEach((paramId, val) -> {
                ProductParameter param = productParameterRepository.findById(paramId)
                        .orElseThrow(() -> new RuntimeException("Invalid Param ID"));
                RequirementValue rv = new RequirementValue();
                rv.setRequirement(savedReq);
                rv.setParameter(param);
                rv.setValue(val);
                valuesList.add(rv);
            });
            requirementValueRepository.saveAll(valuesList);
        }

        return savedReq;
    }
    
 // 1. Get Requirements for a specific Buyer
    public List<Requirements> getBuyerRequirements(Long buyerId) {
        return requirementRepository.findByBuyerId(buyerId); 
        // Note: Ensure you add List<Requirement> findByBuyerId(Long id); to RequirementRepository
    }

    // 2. Get Bids for a specific Requirement
    public List<Bid> getBidsForRequirement(Long requirementId) {
        return bidRepository.findByRequirementId(requirementId);
    }
}
