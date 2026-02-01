package com.bidding.platform.admin.dto;

import lombok.Data;

@Data
public class AdminReqStatsDto {
    private long totalRequirements;
    private long openRequirements;
    private long closedRequirements;
    private long totalBids;
}
