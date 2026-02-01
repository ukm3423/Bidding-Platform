package com.bidding.platform.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidding.platform.admin.dto.AdminReqDetailDto;
import com.bidding.platform.admin.dto.AdminReqListDto;
import com.bidding.platform.admin.dto.AdminReqStatsDto;
import com.bidding.platform.admin.services.AdminRequirementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/requirements")
@RequiredArgsConstructor
public class AdminRequirementController {

    private final AdminRequirementService adminRequirementService;

    @GetMapping("/stats")
    public ResponseEntity<AdminReqStatsDto> getStats() {
        return ResponseEntity.ok(adminRequirementService.getStats());
    }

    @GetMapping
    public ResponseEntity<List<AdminReqListDto>> getAllRequirements() {
        return ResponseEntity.ok(adminRequirementService.getAllRequirements());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AdminReqDetailDto> getRequirementDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminRequirementService.getRequirementDetails(id));
    }
}