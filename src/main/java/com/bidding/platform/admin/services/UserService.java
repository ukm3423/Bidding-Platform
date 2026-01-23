package com.bidding.platform.admin.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bidding.platform.admin.dto.UserListDto;
import com.bidding.platform.admin.dto.UserStatsDto;
import com.bidding.platform.auth.models.Role;
import com.bidding.platform.auth.models.User;
import com.bidding.platform.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public List<UserListDto> getAllUsers(Role role) {
        List<User> users;
        
        if (role != null) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }
        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private UserListDto mapToDto(User user) {
        UserListDto dto = new UserListDto();
        dto.setId(user.getId());
        dto.setFullname(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setRole(user.getRole().name()); // Convert Enum to String
        dto.setStatus(user.getStatus());
        dto.setKycCompleted(user.isKycCompleted());
        return dto;
    }
    public UserStatsDto getUserStats() {
        UserStatsDto stats = new UserStatsDto();
        // 1. Get Total Users (built-in count() method)
        stats.setTotalUsers(userRepository.count());
        // 2. Get Total Buyers
        stats.setTotalBuyers(userRepository.countByRole(Role.BUYER));
        // 3. Get Total Sellers
        stats.setTotalSellers(userRepository.countByRole(Role.SELLER));

        return stats;
    }

    public void updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        userRepository.save(user);
    }

}
