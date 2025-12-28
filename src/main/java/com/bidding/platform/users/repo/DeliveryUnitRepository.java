package com.bidding.platform.users.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.auth.models.DeliveryUnit;

@Repository
public interface DeliveryUnitRepository extends JpaRepository<DeliveryUnit, Long> {
}
