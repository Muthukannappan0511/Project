package com.rvz.dto.employee;


public class BookingDto {

    private Long requestId;
    private String branchName;
    private String roomCode;
    private String startTime;
    private String endTime;
    private String status;
    private String note;
    private String adminComment;

    public BookingDto() {}

    public BookingDto(Long requestId, String branchName, String roomCode,
                      String startTime, String endTime, String status,
                      String note, String adminComment) {
        this.requestId = requestId;
        this.branchName = branchName;
        this.roomCode = roomCode;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.note = note;
        this.adminComment = adminComment;
    }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }
}