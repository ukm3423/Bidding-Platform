package com.bidding.platform.auth.controllers;


import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.auth.dto.JwtRequest;
import com.bidding.platform.auth.dto.JwtResponse;
import com.bidding.platform.auth.dto.OtpRequest;
import com.bidding.platform.auth.dto.SendOtpRequest;
import com.bidding.platform.auth.dto.SendOtpResponse;
import com.bidding.platform.auth.dto.UserDTO;
import com.bidding.platform.auth.models.Token;
import com.bidding.platform.auth.models.TokenType;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.TokenRepository;
import com.bidding.platform.auth.repository.UserRepository;
import com.bidding.platform.auth.security.JwtTokenProvider;
import com.bidding.platform.auth.services.OtpService;
import com.bidding.platform.auth.services.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Tag(name="JWT Authentication")
@RestController
@CrossOrigin
public class JwtAuthController {

    /**
     * * ===========================================================================
     * * ======================== Module : JWTAuthController =======================
     * * ======================== Created By : Umesh Kumar =========================
     * * ======================== Created On : 15-12-2025 ==========================
     * * ===========================================================================
     * * | Code Status : On
     */

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepository tokenRepo;
    
    @Autowired
    private OtpService otpService;


    /**
     * Registers a new buyer or seller on the bidding platform and sends a one-time password (OTP) to the user’s email for verification.
     * This API is used only for new users who do not already have an account.
     * @param req
     * @return
     */
    @PostMapping("/register")
    public UserDTO register(@RequestBody UserDTO req) {

        User user = User.builder()
                .fullname(req.getFullname())
                .email(req.getEmail())
                .role(req.getRole()) // BUYER / SELLER
                .phoneNo(req.getPhoneNo())
                .status("INCOMPLETE")
                .isEmailVerified(false)
                .build();

        userRepo.save(user);

        otpService.sendOtp(user);

        return UserDTO.builder()
                .message("OTP_SENT")
                .build();
    }
    
    /**
     * Verifies the OTP sent to the user’s email and authenticates the user by issuing a JWT access token.
     * This API completes both registration verification and login authentication.
     * @param request
     * @return
     */
    @PostMapping("/verify-otp")
    public JwtResponse verifyOtp(@RequestBody OtpRequest request) {

        // 1. Validate OTP
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            throw new RuntimeException("Invalid OTP");
        }

        // 2. Fetch user
        User user = userRepo.findByEmail(request.getEmail())
        		.orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);
        user.setStatus("ACTIVE");
        userRepo.save(user);

        // 3. MANUAL authentication (NO password)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. Generate JWT
        String jwt = jwtUtil.generateToken(user);

        // 5. Token DB handling (same as before)
        revokeAllUserTokens(user);
        saveUserToken(user, jwt);

        return new JwtResponse(jwt);
    }
    
    /**
     * Sends a login OTP to an already registered user’s email address.
     * This API is used during the login process when a user wants to sign in to their account.
     * @param request
     * @return
     */
    @PostMapping("/send-otp")
    public SendOtpResponse sendOtp(@RequestBody SendOtpRequest request) {

        // 1. Check user exists
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("USER_NOT_REGISTERED"));

        // 2. Check user status
        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("USER_BLOCKED");
        }

        // 3. Send OTP
        otpService.sendOtp(user);

        return new SendOtpResponse("OTP_SENT");
    }

    
    /**
     * * User Login
     * 
     * @param authenticationRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());

        final String token = jwtUtil.generateToken(userDetails);
        User user = userRepo.findByEmail(userDetails.getUsername())
        		.orElseThrow(() -> new RuntimeException("User not found"));
        revokeAllUserTokens(user);
        saveUserToken(user, token);
        System.out.println("\n\nUser Email : " + userDetails.getUsername());
        return new JwtResponse(token);
    }

    /**
     * Authenticate the user by email & password { currently not using}
     * 
     * @param email
     * @param password
     * @throws Exception
     */
    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    /**
     * Save the user's token in database
     * 
     * @param user
     * @param jwtToken
     */
    private void saveUserToken(User user, String jwtToken) {

        var token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        tokenRepo.save(token);
    }

    /**
     * revoked all user's token (if token already available set isExpired = true)
     * 
     * @param user
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId());

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);

    }

    @Value("${spring.app.jwtSecret}")
    private String secretKey;

    @GetMapping("/check-validity")
    public boolean checkTokenValidity(HttpServletRequest request, HttpServletResponse response) {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        if (jwt == null) {
            return false;
        }

        // Parse the token to extract the claims
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            return false; // Token is invalid
        }

        // Check if the token is expired
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            // Mark the token as expired and revoked in the database
            var validUserTokens = tokenRepo.findByToken(jwt);
            if (validUserTokens.isPresent()) {
                Token token = validUserTokens.get();
                token.setExpired(true);
                token.setRevoked(true);
                tokenRepo.save(token);
            }
            return false; // Token is expired
        }

        // Check if the token is valid in the database
        var validUserTokens = tokenRepo.findByToken(jwt);
        if (validUserTokens.isPresent() && !validUserTokens.get().getExpired()
                && !validUserTokens.get().getRevoked()) {
            return true; // Token is valid
        }

        return false; // Token is invalid or not found in the database
    }
    
    @GetMapping("/")
    public String home() {
        return "Application is running 🚀";
    }

}