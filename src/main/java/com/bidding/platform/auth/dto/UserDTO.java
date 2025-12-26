package com.bidding.platform.auth.dto;

import com.bidding.platform.auth.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private Long id;

    private String fullname;

    private String email;

    private Long phoneNo;

    private Role role; // BUYER / SELLER

    private String message; // OTP_SENT, VERIFIED, ERROR
}
