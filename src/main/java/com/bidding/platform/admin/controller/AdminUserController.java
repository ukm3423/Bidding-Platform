package com.bidding.platform.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.admin.dto.UserListDto;
import com.bidding.platform.admin.dto.UserStatsDto;
import com.bidding.platform.admin.services.UserService;
import com.bidding.platform.auth.models.Role;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/admin/users")
@RequiredArgsConstructor
@CrossOrigin
public class AdminUserController {
	
	@Autowired
	private UserService userService;
	
	// GET /admin/users  (Returns everyone)
    // GET /admin/users?role=SELLER  (Returns only Sellers)
    @GetMapping
    public ResponseEntity<List<UserListDto>> getAllUsers(@RequestParam(required = false) Role role) {
        return ResponseEntity.ok(userService.getAllUsers(role));
    }
    
 // GET /admin/users/stats
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDto> getUserStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }
    
}
