package com.bidding.platform.buyer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.buyer.model.Requirements;
@Repository
public interface RequirementRepository extends JpaRepository<Requirements, Long>{

	List<Requirements> findByBuyerId(Long buyerId);
	
	// NEW: Find all Open requirements (Latest first)
    List<Requirements> findByStatusOrderByCreatedAtDesc(String status);

}
