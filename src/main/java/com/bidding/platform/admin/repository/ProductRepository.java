package com.bidding.platform.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	Boolean existsByName(String name);
	
	Boolean existsByNameAndCategory(String name, Category category);
	
	List<Product> findByCategory(Category category);

	Optional<Product> findByNameAndCategory(String name, Category category);
	
}
