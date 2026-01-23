package com.bidding.platform.seller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.seller.model.Bid;
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByRequirementId(Long requirementId);
    
 // Fetch all bids for a specific seller
    List<Bid> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    // Optional: Fetch by status if you want server-side filtering later
    List<Bid> findBySellerIdAndStatus(Long sellerId, String status);
}
