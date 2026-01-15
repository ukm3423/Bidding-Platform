package com.bidding.platform.buyer.model;

import com.bidding.platform.admin.model.ProductParameter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "requirement_values")
public class RequirementValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requirement_id")
    @JsonBackReference
    private Requirements requirement;

    // Which parameter is this answer for?
    @ManyToOne
    @JoinColumn(name = "parameter_id")
    private ProductParameter parameter;

    // The actual answer typed by the buyer
    @Column(name = "param_value")
    private String value; 
}
