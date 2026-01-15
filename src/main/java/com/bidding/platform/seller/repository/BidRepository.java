package com.bidding.platform.seller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.seller.model.Bid;
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByRequirementId(Long requirementId);
}
