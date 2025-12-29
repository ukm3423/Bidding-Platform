package com.bidding.platform.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bidding.platform.auth.models.Company;
import com.bidding.platform.auth.models.ContactDetails;
import com.bidding.platform.auth.models.Role;
import com.bidding.platform.auth.models.SellerKyc;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.common.dto.ApiResponse;
import com.bidding.platform.common.services.FileService;
import com.bidding.platform.users.repo.CompanyRepository;
import com.bidding.platform.users.repo.ContactDetailsRepository;
import com.bidding.platform.users.repo.SellerKycRepository;

@CrossOrigin
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Autowired
	private FileService fileService;

	@Autowired
	private CompanyRepository companyRepo;

	@Autowired
	private ContactDetailsRepository contactRepo;

	@Autowired
	private SellerKycRepository sellerKycRepo;

	@Autowired
	private UserRepository userRepo;

	@Transactional
	@PostMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse completeSellerProfile(@RequestParam String companyName, @RequestParam String gstNumber,
			@RequestParam String mobileNumber, @RequestParam(required = false) String alternateMobileNumber,
			@RequestParam MultipartFile gstCertificate, @RequestParam MultipartFile businessCertificate,
			@RequestParam MultipartFile turnoverCertificate,
			Authentication auth) {

		User user = (User) auth.getPrincipal();

		if (user.getRole() != Role.SELLER) {
			throw new RuntimeException("INVALID_ROLE");
		}

		// Upload documents to MinIO
		String gstUrl = fileService.upload(gstCertificate);
		String businessUrl = fileService.upload(businessCertificate);
		String turnoverUrl = fileService.upload(turnoverCertificate);

		// Company
		Company company = new Company();
		company.setUser(user);
		company.setCompanyName(companyName);
		company.setGstNumber(gstNumber);
		companyRepo.save(company);

		// Contact Details
		contactRepo.save(new ContactDetails(null, user, mobileNumber, alternateMobileNumber));

		// Seller KYC
		sellerKycRepo.save(SellerKyc.builder().user(user).gstNumber(gstNumber).gstCertificateUrl(gstUrl)
				.businessCertificateUrl(businessUrl).turnoverCertificateUrl(turnoverUrl).kycStatus("SUBMITTED")
				.build());

		// Update user status
		user.setKycCompleted(true);
		user.setStatus("PENDING_APPROVAL");
		userRepo.save(user);

		return new ApiResponse("SELLER_PROFILE_SUBMITTED");
	}

}
