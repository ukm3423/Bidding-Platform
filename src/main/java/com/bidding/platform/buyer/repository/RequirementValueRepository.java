package com.bidding.platform.buyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.buyer.model.RequirementValue;
@Repository
public interface RequirementValueRepository extends JpaRepository<RequirementValue, Long>{

}
