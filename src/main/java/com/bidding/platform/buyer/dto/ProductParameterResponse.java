package com.bidding.platform.buyer.dto;

import java.util.List;

import com.bidding.platform.admin.model.DataType;

import lombok.Data;

@Data
public class ProductParameterResponse {
    private Long id;
    private String paramName;
    private boolean mandatory;
    private DataType inputType; // TEXT, NUMBER, DROPDOWN
    private List<String> options; // for dropdown
}