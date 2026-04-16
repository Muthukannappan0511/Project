package com.rvz.dto.employee;

public class BookingResponseDto {

    private Long requestId;
    private String roomCode;
    private String branchName;
    private String startTime;
    private String endTime;
    private String status;
    private String employeeNote;
    private String adminComment;

    // No-args constructor
    public BookingResponseDto() {
    }

    // All-args constructor
    public BookingResponseDto(
            Long requestId,
            String roomCode,
            String branchName,
            String startTime,
            String endTime,
            String status,
            String employeeNote,
            String adminComment
    ) {
        this.requestId = requestId;
        this.roomCode = roomCode;
        this.branchName = branchName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.employeeNote = employeeNote;
        this.adminComment = adminComment;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployeeNote() {
        return employeeNote;
    }

    public void setEmployeeNote(String employeeNote) {
        this.employeeNote = employeeNote;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}