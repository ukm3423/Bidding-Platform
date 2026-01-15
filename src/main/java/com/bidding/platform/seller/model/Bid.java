package com.bidding.platform.seller.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.bidding.platform.auth.models.User;
import com.bidding.platform.buyer.model.Requirements;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which requirement is this for?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    @JsonIgnoreProperties({"bids", "buyer", "values"}) // Prevent infinite loops
    private Requirements requirement;

    // Who is the seller?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"tokens", "password"})
    private User seller;

    private Double bidAmount;
    private String status; // "PENDING", "ACCEPTED", "REJECTED"
    
    // Delivery terms proposed by seller
    private String deliveryTime; // e.g. "6-8 days"
    private Integer moq;         // Minimum Order Quantity
    private Integer offeredQty;  // Quantity they can supply

    @CreationTimestamp
    private LocalDateTime createdAt;
}
