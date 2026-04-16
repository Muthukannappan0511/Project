package com.rvz.dto;

public class SuperAdminDashboardDto {

    private long totalBranches;
    private long totalUsers;
    private long totalRooms;   // ✅ NEW

    public SuperAdminDashboardDto() {}

    public SuperAdminDashboardDto(long totalBranches, long totalUsers, long totalRooms) {
        this.totalBranches = totalBranches;
        this.totalUsers = totalUsers;
        this.totalRooms = totalRooms;
    }

    public long getTotalBranches() { return totalBranches; }
    public void setTotalBranches(long totalBranches) { this.totalBranches = totalBranches; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }
}