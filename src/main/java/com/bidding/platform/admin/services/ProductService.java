package com.bidding.platform.admin.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bidding.platform.admin.dto.CategoryCreateRequest;
import com.bidding.platform.admin.dto.CategoryResponseDTO;
import com.bidding.platform.admin.dto.CategoryUpdateRequest;
import com.bidding.platform.admin.dto.ProductCreateRequest;
import com.bidding.platform.admin.dto.ProductParameterDto;
import com.bidding.platform.admin.dto.ProductResponse;
import com.bidding.platform.admin.model.Category;
import com.bidding.platform.admin.model.Product;
import com.bidding.platform.admin.model.ProductParameter;
import com.bidding.platform.admin.repository.CategoryRepository;
import com.bidding.platform.admin.repository.ProductParameterRepository;
import com.bidding.platform.admin.repository.ProductRepository;
import com.bidding.platform.common.dto.ErrorCode;
import com.bidding.platform.common.exceptions.BusinessException;
import com.bidding.platform.common.services.FileService;
import com.bidding.platform.common.services.MinioService;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private MinioService minioService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ProductParameterRepository productParameterRepository;
	
	@Transactional
	public Category createCategory(CategoryCreateRequest request) {

	    String imageUrl = fileService.upload(request.getImage());

	    Category category = new Category();
	    category.setName(request.getName());
	    category.setDescription(request.getDescription());
	    category.setCategoryUrl(imageUrl);

	    return categoryRepository.save(category);
	}

	
	public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByIdDesc();

        return categories.stream()
                .map(cat -> CategoryResponseDTO.builder()
                		.id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .imageUrl(cat.getCategoryUrl() != null
                                ? minioService.generatePresignedUrl(cat.getCategoryUrl())
                                : null
                        )
                        .build()
                )
                .collect(Collectors.toList());
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
       
		String imageUrl = fileService.upload(request.getImage());

	    // 4. Create and Save Product
	    Product product = new Product();
	    product.setCategory(category);
	    product.setName(request.getName());
	    product.setDescription(request.getDescription());
	    product.setProductUrl(imageUrl);
	    product.setIsActive(true);

	    return productRepository.save(product);
	}
	
    // -----------------------------
    // GET ALL PRODUCTS
    // -----------------------------
	public List<ProductResponse> getAllProducts() {

	    List<Product> products = productRepository.findAll();

	    return products.stream().map(product -> {

	        String presignedUrl = minioService.generatePresignedUrl(product.getProductUrl());

	        return new ProductResponse(
	                product.getId(),
	                product.getName(),
	                product.getDescription(),
	                product.getIsActive(),
	                presignedUrl,                         // FULL IMAGE URL
	                new CategoryResponseDTO(
	                        product.getCategory().getId(),
	                        product.getCategory().getName(), 
	                        product.getCategory().getDescription(), 
	                        product.getCategory().getCategoryUrl()
	                ),
	                product.getParameters().stream().map(param -> 
	                    new ProductParameterDto(
	                        param.getParamName(),
	                        param.getDataType(),
	                        param.getIsMandatory(),
	                        "null"
	                    )
	                ).toList()
	        );

	    }).toList();
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
    
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryUpdateRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found with ID: " + id
                ));

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            category.setDescription(request.getDescription());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {

            if (category.getCategoryUrl() != null) {
                fileService.delete(category.getCategoryUrl());
            }

            String newUrl = fileService.upload(request.getImage());
            category.setCategoryUrl(newUrl);
        }

        Category saved = categoryRepository.save(category);

        return CategoryResponseDTO.builder()
        		.id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .imageUrl(saved.getCategoryUrl() != null
                        ? minioService.generatePresignedUrl(saved.getCategoryUrl())
                        : null
                )
                .build();
    }


    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found with ID: " + id));

        if (category.getCategoryUrl() != null) {
            fileService.delete(category.getCategoryUrl());
        }

        categoryRepository.delete(category);
    }


	
}
