package com.bidding.platform.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "OTP is required")
    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "OTP must be 6 digits"
    )
    @NotBlank(message = "OTP is required")
    private String otp;
    
    @NotBlank(message = "Role is required")
    private String role;
}