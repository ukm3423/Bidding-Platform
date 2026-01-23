package com.bidding.platform.seller.dto;

import lombok.Data;

@Data
public class BidStatsDto {
    private long totalBids;
    private long pending;
    private long shortlisted;
    private long awarded;
    private long rejected;
}
