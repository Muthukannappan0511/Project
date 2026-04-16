package com.rvz.dto;

import java.time.LocalDateTime;
public class BranchAdminDashboardDto {

    // My Logs response
    public record MyLogDto(
            Long logId,
            String roomCode,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer occupancy
    ) {}

    // Dashboard summary counts
    public record BranchSummaryDto(
            long membersCount,
            long logsCount,
            long roomsCount
    ) {}
}

