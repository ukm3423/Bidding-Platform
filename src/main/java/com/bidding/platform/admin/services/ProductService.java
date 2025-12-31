package com.bidding.platform.admin.services;

import java.util.List;
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
	
	//Added for Creation of Products 30/12/2025
	@Transactional
	public Product createProduct(ProductCreateRequest request) {
	    if (request.getCategoryId() == null) {
	        throw new IllegalArgumentException("Category ID cannot be null");
	    }
	    if (request.getName() == null || request.getName().isEmpty()) {
	        throw new IllegalArgumentException("Product Name cannot be empty");
	    }

	    if (productRepository.existsByName(request.getName())) {
	        throw new RuntimeException("Product with this name already exists");
	    }

	    Category category = categoryRepository.findById(request.getCategoryId())
	        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));

	    // 4. Create and Save Product
	    Product product = new Product();
	    product.setCategory(category);
	    product.setName(request.getName());
	    product.setDescription(request.getDescription());
	    product.setIsActive(true);

	    return productRepository.save(product);
	}
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // Added the Addition of Params in Products
    @Transactional
    public Product addParameters(Long productId, List<ProductParameterDto> parameterDtos) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        List<ProductParameter> parameters = parameterDtos.stream().map(dto -> {
            ProductParameter param = new ProductParameter();
            param.setProduct(product);
            param.setParamName(dto.getParamName());
            param.setDataType(dto.getDataType());
            param.setIsMandatory(dto.isMandatory());
            return param;
        }).toList();
        productParameterRepository.saveAll(parameters);
        return product;
    }
    
	
	
}
