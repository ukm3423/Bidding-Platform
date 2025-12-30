package com.bidding.platform.buyer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.auth.models.Company;
import com.bidding.platform.auth.models.ContactDetails;
import com.bidding.platform.auth.models.DeliveryUnit;
import com.bidding.platform.auth.models.Role;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.models.UserProductInterest;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.buyer.dto.BuyerProfileRequest;
import com.bidding.platform.common.dto.ApiResponse;
import com.bidding.platform.users.repo.CompanyRepository;
import com.bidding.platform.users.repo.ContactDetailsRepository;
import com.bidding.platform.users.repo.DeliveryUnitRepository;
import com.bidding.platform.users.repo.UserProductInterestRepository;

@CrossOrigin
@RestController
@RequestMapping("/buyer")
public class BuyerController {
	
	@Autowired
	private CompanyRepository companyRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ContactDetailsRepository contactRepo;

    @Autowired
    private DeliveryUnitRepository deliveryRepo;

    @Autowired
    private UserProductInterestRepository interestRepo;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Welcome to the User Dashboard";
    }
    
    
    @PostMapping("/complete")
    public ApiResponse completeBuyerProfile(
            @RequestBody BuyerProfileRequest req,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        if (user.getRole() != Role.BUYER) {
            throw new RuntimeException("INVALID_ROLE");
        }

        // Company
        Company company = new Company();
        company.setUser(user);
        company.setCompanyName(req.getCompanyName());
        companyRepo.save(company);

        // Contact
        contactRepo.save(new ContactDetails(
                null,
                user,
                req.getMobileNumber(),
                req.getAlternateMobileNumber()
        ));

        // Delivery Unit
        deliveryRepo.save(new DeliveryUnit(
                null,
                company,
                req.getUnitName(),
                req.getUnitType(),
                req.getStreet(),
                req.getLocality(),
                req.getCity(),
                req.getState(),
                req.getPincode()
        ));

        // Product Interest
        interestRepo.deleteByUserId(user.getId());
        req.getProductIds().forEach(pid ->
            interestRepo.save(new UserProductInterest(null, user.getId(), pid))
        );

        // Activate buyer
        user.setStatus("ACTIVE");
        userRepo.save(user);

        return new ApiResponse("BUYER_PROFILE_COMPLETED");
    }

    
}