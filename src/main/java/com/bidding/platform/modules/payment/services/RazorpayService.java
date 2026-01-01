package com.bidding.platform.modules.payment.services;

import java.util.Map;

import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.modules.payment.dto.RazorpayVerifyDto;
import com.bidding.platform.modules.payment.models.RegistrationPayment;
import com.bidding.platform.modules.payment.repository.RegistrationPaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    private final UserRepository userRepository;
    private final RegistrationPaymentRepository paymentRepo;

    public Map<String, Object> createOrder(String email) {
        try {
            RazorpayClient client = new RazorpayClient(key, secret);

            JSONObject options = new JSONObject();
            options.put("amount", 5000 * 100); // ₹5000
            options.put("currency", "INR");

            Order order = client.orders.create(options);

            User seller = userRepository.findByEmail(email).orElseThrow();

            RegistrationPayment payment = new RegistrationPayment();
            payment.setOrderId(order.get("id"));
            payment.setAmount(5000L);
            payment.setStatus("CREATED");
            payment.setSeller(seller);
            paymentRepo.save(payment);

            return Map.of(
                "orderId", order.get("id"),
                "amount", order.get("amount")
            );

        } catch (Exception e) {
        	e.printStackTrace(); 
            throw new RuntimeException("Order creation failed: " + e.getMessage());
        }
    }

    public void verifyPayment(RazorpayVerifyDto dto, String email) {

//        String payload = dto.getRazorpayOrderId() + "|"
//                + dto.getRazorpayPaymentId();
//
//        String generatedSignature =
//                HmacUtils.hmacSha256Hex(secret, payload);
//
//        if (!generatedSignature.equals(dto.getRazorpaySignature())) {
//            throw new RuntimeException("Invalid payment signature");
//        }

        RegistrationPayment payment =
                paymentRepo.findByOrderId(dto.getRazorpayOrderId())
                        .orElseThrow();

        payment.setPaymentId(dto.getRazorpayPaymentId());
        payment.setSignature(dto.getRazorpaySignature());
        payment.setStatus("PAID");
        paymentRepo.save(payment);

        User seller = payment.getSeller();
        seller.setPaymentCompleted(true);
        userRepository.save(seller);
    }
}
