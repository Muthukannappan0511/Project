package com.rvz.dto;

import java.util.List;

public class SuperAdminReportDto {

    public static class BranchSlice {

        private Long branchId;
        private String branchName;
        private long usageHours;
        private long logsCount;
        private double percent; 

        public BranchSlice() {
        }

        public BranchSlice(
                Long branchId,
                String branchName,
                long usageHours,
                long logsCount,
                double percent
        ) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.usageHours = usageHours;
            this.logsCount = logsCount;
            this.percent = percent;
        }

        public Long getBranchId() {
            return branchId;
        }

        public void setBranchId(Long branchId) {
            this.branchId = branchId;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public long getUsageHours() {
            return usageHours;
        }

        public void setUsageHours(long usageHours) {
            this.usageHours = usageHours;
        }

        public long getLogsCount() {
            return logsCount;
        }

        public void setLogsCount(long logsCount) {
            this.logsCount = logsCount;
        }

        public double getPercent() {
            return percent;
        }

        public void setPercent(double percent) {
            this.percent = percent;
        }
    }

    /* -------------------- Right Panel Insights (Branch-wise) -------------------- */
    public static class BranchInsights {

        private Long branchId;
        private String branchName;
        private String peakUsageTime;
        private String mostUsedRoom;
        private String leastUsedRoom;
        private long logsCount;
        private long usageHours;

        public BranchInsights() {
        }

        public BranchInsights(
                Long branchId,
                String branchName,
                String peakUsageTime,
                String mostUsedRoom,
                String leastUsedRoom,
                long logsCount,
                long usageHours
        ) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.peakUsageTime = peakUsageTime;
            this.mostUsedRoom = mostUsedRoom;
            this.leastUsedRoom = leastUsedRoom;
            this.logsCount = logsCount;
            this.usageHours = usageHours;
        }

        public Long getBranchId() {
            return branchId;
        }

        public void setBranchId(Long branchId) {
            this.branchId = branchId;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getPeakUsageTime() {
            return peakUsageTime;
        }

        public void setPeakUsageTime(String peakUsageTime) {
            this.peakUsageTime = peakUsageTime;
        }

        public String getMostUsedRoom() {
            return mostUsedRoom;
        }

        public void setMostUsedRoom(String mostUsedRoom) {
            this.mostUsedRoom = mostUsedRoom;
        }

        public String getLeastUsedRoom() {
            return leastUsedRoom;
        }

        public void setLeastUsedRoom(String leastUsedRoom) {
            this.leastUsedRoom = leastUsedRoom;
        }

        public long getLogsCount() {
            return logsCount;
        }

        public void setLogsCount(long logsCount) {
            this.logsCount = logsCount;
        }

        public long getUsageHours() {
            return usageHours;
        }

        public void setUsageHours(long usageHours) {
            this.usageHours = usageHours;
        }
    }

    /* -------------------- Root DTO Fields -------------------- */
    private List<BranchSlice> branches;
    private BranchInsights selectedBranch;

    public SuperAdminReportDto() {
    }

    public SuperAdminReportDto(
            List<BranchSlice> branches,
            BranchInsights selectedBranch
    ) {
        this.branches = branches;
        this.selectedBranch = selectedBranch;
    }

    public List<BranchSlice> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchSlice> branches) {
        this.branches = branches;
    }

    public BranchInsights getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(BranchInsights selectedBranch) {
        this.selectedBranch = selectedBranch;
    }
}