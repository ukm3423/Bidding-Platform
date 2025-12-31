package com.bidding.platform.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.admin.model.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	boolean existsByName(String name);

}
