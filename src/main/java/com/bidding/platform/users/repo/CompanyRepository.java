package com.bidding.platform.users.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.auth.models.Company;
import com.bidding.platform.auth.models.SellerKyc;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{

	Company findByUserId(Long userId);
}
