package com.bidding.platform.modules.payment;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.modules.payment.dto.RazorpayVerifyDto;
import com.bidding.platform.modules.payment.models.RegistrationPayment;
import com.bidding.platform.modules.payment.repository.RegistrationPaymentRepository;
import com.bidding.platform.modules.payment.services.RazorpayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments/registration")
@RequiredArgsConstructor
public class RegistrationPaymentController {

    private final RazorpayService razorpayService;
    
    private final UserRepository userRepository;
    
    private final RegistrationPaymentRepository paymentRepo;

    @PostMapping("/create-order")
    public Map<String, Object> createOrder(Authentication auth) {
        return razorpayService.createOrder(auth.getName());
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody RazorpayVerifyDto dto,
            Authentication auth) {

        razorpayService.verifyPayment(dto, auth.getName());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/mock-success")
    public ResponseEntity<?> mockPayment(Authentication auth) {

        User seller = userRepository
                .findByEmail(auth.getName())
                .orElseThrow();

        // Prevent double payment
        if (seller.isPaymentCompleted()) {
            return ResponseEntity.badRequest()
                    .body("Registration already completed");
        }

        RegistrationPayment payment = new RegistrationPayment();
        payment.setOrderId("MOCK_ORDER_" + System.currentTimeMillis());
        payment.setPaymentId("MOCK_PAYMENT_" + System.currentTimeMillis());
        payment.setAmount(5000L);
        payment.setStatus("PAID");
        payment.setSeller(seller);

        paymentRepo.save(payment);

        seller.setPaymentCompleted(true);
        userRepository.save(seller);

        return ResponseEntity.ok().build();
    }
}
