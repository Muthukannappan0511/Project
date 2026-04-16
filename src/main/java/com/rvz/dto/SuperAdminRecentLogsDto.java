package com.rvz.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SuperAdminRecentLogsDto {

    public static class RecentLog {
        private String roomCode;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer occupancy;
        private String createdBy;

        public RecentLog(String roomCode, LocalDateTime startTime,
                         LocalDateTime endTime, Integer occupancy, String createdBy) {
            this.roomCode = roomCode;
            this.startTime = startTime;
            this.endTime = endTime;
            this.occupancy = occupancy;
            this.createdBy = createdBy;
        }

        public String getRoomCode() { return roomCode; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public Integer getOccupancy() { return occupancy; }
        public String getCreatedBy() { return createdBy; }
    }

    // Branch wise table
    public static class BranchLogs {
        private Long branchId;
        private String branchName;
        private List<RecentLog> logs;

        public BranchLogs(Long branchId, String branchName, List<RecentLog> logs) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.logs = logs;
        }

        public Long getBranchId() { return branchId; }
        public String getBranchName() { return branchName; }
        public List<RecentLog> getLogs() { return logs; }
    }
}
