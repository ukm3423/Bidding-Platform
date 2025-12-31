package com.bidding.platform.admin.dto;

import lombok.Data;

@Data
public class UserListDto {
	private Long id;
    private String fullname;
    private String email;
    private Long phoneNo;
    private String role;           // "BUYER", "SELLER"
    private String status;         // "ACTIVE", "INCOMPLETE"
    private boolean isKycCompleted;

}
