package com.bidding.platform.admin.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bidding.platform.admin.dto.ProductCreateRequest;
import com.bidding.platform.admin.dto.ProductParameterDto;
import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.model.ProductParameter;
import com.bidding.platform.admin.repository.CategoryRepository;
import com.bidding.platform.admin.repository.ProductParameterRepository;
import com.bidding.platform.admin.repository.ProductRepository;
import com.bidding.platform.common.dto.ErrorCode;
import com.bidding.platform.common.exceptions.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductParameterRepository productParameterRepository;
	
	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}
	public List<Category> getAllCategories(){
		return categoryRepository.findAll();
	}
	
    // -----------------------------
    // CREATE PRODUCT
    // -----------------------------
	@Transactional
	public Product createProduct(ProductCreateRequest request) {
	    
	    Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found with ID: " + request.getCategoryId()
                ));

	    if (productRepository.existsByNameAndCategory(request.getName(), category)) {
            throw new BusinessException(
                    ErrorCode.PRODUCT_ALREADY_EXISTS,
                    "Product with this name already exists in this category"
            );
        }

	    // 4. Create and Save Product
	    Product product = new Product();
	    product.setCategory(category);
	    product.setName(request.getName());
	    product.setDescription(request.getDescription());
	    product.setIsActive(true);

	    return productRepository.save(product);
	}
	
    // -----------------------------
    // GET ALL PRODUCTS
    // -----------------------------
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // -----------------------------
    // ADD PARAMETERS TO PRODUCT
    // -----------------------------
    @Transactional
    public Product addParameters(Long productId, List<ProductParameterDto> parameterDtos) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with ID: " + productId
                ));

        if (parameterDtos == null || parameterDtos.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "Parameters list cannot be empty"
            );
        }
        
        Set<String> names = new HashSet<>();
        for (ProductParameterDto dto : parameterDtos) {
            if (!names.add(dto.getParamName().trim())) {
                throw new BusinessException(
                        ErrorCode.DUPLICATE_PARAMETER,
                        "Duplicate parameter name: " + dto.getParamName()
                );
            }
        }
        
        List<ProductParameter> parameters = parameterDtos.stream().map(dto -> {
            ProductParameter param = new ProductParameter();
            param.setProduct(product);
            param.setParamName(dto.getParamName());
            param.setDataType(dto.getDataType());
            param.setIsMandatory(dto.getIsMandatory());
            return param;
        }).toList();
        productParameterRepository.saveAll(parameters);
        return product;
    }
    
	
	
}
