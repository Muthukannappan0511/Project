package com.rvz.dto;

import java.util.List;

public class ManagerReportDto {

    public static class SpaceUtilizationRow {

        private Long roomId;
        private String spaceName;   
        private String type;        
        private double utilization;
        public SpaceUtilizationRow() {
        }

        public SpaceUtilizationRow(
                Long roomId,
                String spaceName,
                String type,
                double utilization
        ) {
            this.roomId = roomId;
            this.spaceName = spaceName;
            this.type = type;
            this.utilization = utilization;
        }

        public Long getRoomId() {
            return roomId;
        }

        public void setRoomId(Long roomId) {
            this.roomId = roomId;
        }

        public String getSpaceName() {
            return spaceName;
        }

        public void setSpaceName(String spaceName) {
            this.spaceName = spaceName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getUtilization() {
            return utilization;
        }

        public void setUtilization(double utilization) {
            this.utilization = utilization;
        }
    }

    public static class KeyInsights {

        private String mostUsedSpace;
        private String leastUsedSpace;
        private String peakUsageTime;

        public KeyInsights() {
        }

        public KeyInsights(
                String mostUsedSpace,
                String leastUsedSpace,
                String peakUsageTime
        ) {
            this.mostUsedSpace = mostUsedSpace;
            this.leastUsedSpace = leastUsedSpace;
            this.peakUsageTime = peakUsageTime;
        }

        public String getMostUsedSpace() {
            return mostUsedSpace;
        }

        public void setMostUsedSpace(String mostUsedSpace) {
            this.mostUsedSpace = mostUsedSpace;
        }

        public String getLeastUsedSpace() {
            return leastUsedSpace;
        }

        public void setLeastUsedSpace(String leastUsedSpace) {
            this.leastUsedSpace = leastUsedSpace;
        }

        public String getPeakUsageTime() {
            return peakUsageTime;
        }

        public void setPeakUsageTime(String peakUsageTime) {
            this.peakUsageTime = peakUsageTime;
        }
    }

    // -------- Final response --------
    private List<SpaceUtilizationRow> rows;
    private KeyInsights insights;

    public ManagerReportDto() {
    }

    public ManagerReportDto(
            List<SpaceUtilizationRow> rows,
            KeyInsights insights
    ) {
        this.rows = rows;
        this.insights = insights;
    }

    public List<SpaceUtilizationRow> getRows() {
        return rows;
    }

    public void setRows(List<SpaceUtilizationRow> rows) {
        this.rows = rows;
    }

    public KeyInsights getInsights() {
        return insights;
    }

    public void setInsights(KeyInsights insights) {
        this.insights = insights;
    }
}