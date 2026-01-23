package com.bidding.platform.buyer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.model.ProductParameter;
import com.bidding.platform.admin.repository.CategoryRepository;
import com.bidding.platform.admin.repository.ProductParameterRepository;
import com.bidding.platform.admin.repository.ProductRepository;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.buyer.dto.RequirementPostRequest;
import com.bidding.platform.buyer.model.RequirementDocument;
import com.bidding.platform.buyer.model.RequirementValue;
import com.bidding.platform.buyer.model.Requirements;
import com.bidding.platform.buyer.repository.RequirementDocumentRepository;
import com.bidding.platform.buyer.repository.RequirementRepository;
import com.bidding.platform.buyer.repository.RequirementValueRepository;
import com.bidding.platform.common.dto.ErrorCode;
import com.bidding.platform.common.exceptions.BusinessException;
import com.bidding.platform.common.services.FileService;
import com.bidding.platform.seller.model.Bid;
import com.bidding.platform.seller.repository.BidRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductParameterRepository productParameterRepository;
    private final RequirementValueRepository requirementValueRepository;
    private final RequirementDocumentRepository requirementDocumentRepository;
    private final FileService fileService;
    private final BidRepository bidRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Requirements postRequirement(
            Long buyerId,
            RequirementPostRequest request,
            MultipartFile[] files
    ) {

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("BUYER_NOT_FOUND"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found"
                ));

        Product product = productRepository
                .findByNameAndCategory(request.getProductName(), category)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found in selected category"
                ));



        // =============================
        // BUSINESS VALIDATION
        // =============================
        if (request.isSplitOrderAllowed() && request.getMinQuantity() == null) {
            throw new BusinessException(
                ErrorCode.INVALID_DATA,
                "Min quantity is required when split order is allowed"
            );
        }

        // =====================================================
        // NEW: VALIDATE MANDATORY DYNAMIC PARAMETERS (ADD HERE)
        // =====================================================
        List<ProductParameter> mandatoryParams =
                productParameterRepository
                        .findByProductIdAndIsMandatoryTrue(product.getId());

        for (ProductParameter param : mandatoryParams) {

            if (request.getDynamicValues() == null ||
                !request.getDynamicValues().containsKey(param.getId()) ||
                request.getDynamicValues().get(param.getId()) == null ||
                request.getDynamicValues().get(param.getId()).isBlank()) {

                throw new BusinessException(
                    ErrorCode.MISSING_PARAMETER,
                    param.getParamName() + " is mandatory"
                );
            }
        }
        // =============================
        // 🔥 END VALIDATION
        // =============================


        // =============================
        // STEP 1 + STEP 2 DATA
        // =============================
        Requirements requirement = new Requirements();
        requirement.setBuyer(buyer);
        requirement.setProduct(product);

        requirement.setQuantity(request.getQuantity());
        requirement.setQuantityUnit(request.getQuantityUnit());
        requirement.setSplitOrderAllowed(request.isSplitOrderAllowed());
        requirement.setMinQuantity(request.getMinQuantity());
        requirement.setRejectionClause(request.getRejectionClause());
        requirement.setAdditionalDetails(request.getAdditionalDetails());

        requirement.setDeliveryUnitName(request.getDeliveryUnitName());
        requirement.setDeliveryStreet(request.getDeliveryStreet());
        requirement.setDeliveryLocality(request.getDeliveryLocality());
        requirement.setDeliveryCity(request.getDeliveryCity());
        requirement.setDeliveryState(request.getDeliveryState());
        requirement.setDeliveryPincode(request.getDeliveryPincode());
        requirement.setDeliveryPeriodDays(request.getDeliveryPeriodDays());

        requirement.setBudgetAmount(request.getBudgetAmount());
        requirement.setBudgetType(request.getBudgetType());
        requirement.setPaymentTerms(request.getPaymentTerms());
        requirement.setShippingPreference(request.getShippingPreference());

        requirement.setStatus("OPEN");

        Requirements savedRequirement =
                requirementRepository.save(requirement);

        // =============================
        // DYNAMIC PARAMETERS (SAVE)
        // =============================
        if (request.getDynamicValues() != null && !request.getDynamicValues().isEmpty()) {

            List<RequirementValue> values = new ArrayList<>();

            request.getDynamicValues().forEach((paramId, value) -> {

                ProductParameter param =
                        productParameterRepository.findById(paramId)
                                .orElseThrow(() -> new BusinessException(
                                    ErrorCode.INVALID_PARAMETER_ID,
                                    "INVALID PARAMETER ID"
                                ));

                RequirementValue rv = new RequirementValue();
                rv.setRequirement(savedRequirement);
                rv.setParameter(param);
                rv.setValue(value);

                values.add(rv);
            });

            requirementValueRepository.saveAll(values);
            savedRequirement.setValues(values);
        }

        // =============================
        // DOCUMENT UPLOAD (MINIO)
        // =============================
        if (files != null && files.length > 0) {

            List<RequirementDocument> documents = new ArrayList<>();

            for (MultipartFile file : files) {

                String objectName = fileService.upload(file);

                RequirementDocument doc = new RequirementDocument();
                doc.setRequirement(savedRequirement);
                doc.setObjectName(objectName);
                doc.setOriginalName(file.getOriginalFilename());
                doc.setContentType(file.getContentType());
                doc.setFileSize(file.getSize());

                documents.add(doc);
            }

            requirementDocumentRepository.saveAll(documents);
            savedRequirement.setDocuments(documents);
        }

        return savedRequirement;
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
