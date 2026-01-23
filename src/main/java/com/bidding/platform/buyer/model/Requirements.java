package com.bidding.platform.buyer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.bidding.platform.admin.model.Product;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.seller.model.Bid;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Data
@Table(name = "requirements")
public class Requirements {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    // What product is it?
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Double quantity;    // e.g., 500
    private String quantityUnit;// e.g., "MT" or "Tons"
    
    private boolean splitOrderAllowed; // "Split Order" checkbox
    private Double minQuantity;        // If split is allowed
    
    @Column(columnDefinition = "TEXT")
    private String rejectionClause;    // "Rejection Clause"
    
    @Column(columnDefinition = "TEXT")
    private String additionalDetails; 

    // --- STEP 2: DELIVERY & BUDGET ---
    // Address (Could be a separate Embeddable, but flat columns are easier for now)
    private String deliveryUnitName;
    private String deliveryStreet;
    private String deliveryLocality;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPincode;

    private Integer deliveryPeriodDays; // "Delivery Period (days)"

    private Double budgetAmount;        // Optional
    private String budgetType;          // "Per Unit" or "Total"
    
    private String paymentTerms;        // Dropdown value
    private String shippingPreference;  // "Seller Arranged" etc.

    // Status
    private String status = "OPEN";

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Dynamic Specs (Fe Content, Silica, etc.)
    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RequirementValue> values = new ArrayList<>();
    
    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("requirement") // Stop recursion
    private List<Bid> bids = new ArrayList<>();
    
    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("requirement")
    private List<RequirementDocument> documents = new ArrayList<>();
}
