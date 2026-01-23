package com.bidding.platform.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.ProductParameter;


@Repository
public interface ProductParameterRepository extends JpaRepository<ProductParameter, Long>{

	Optional<ProductParameter> findByIdAndProductId(Long paramId, Long productId);
	
	List<ProductParameter> findByProductIdAndIsMandatoryTrue(Long productId);

}
