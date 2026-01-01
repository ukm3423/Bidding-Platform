package com.bidding.platform.modules.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bidding.platform.modules.payment.models.RegistrationPayment;

public interface RegistrationPaymentRepository extends JpaRepository<RegistrationPayment, Long> {

	Optional<RegistrationPayment> findByOrderId(String orderId);
}
