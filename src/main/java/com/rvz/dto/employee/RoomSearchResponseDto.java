package com.rvz.dto.employee;

public class RoomSearchResponseDto {

    private Long roomId;
    private String roomCode;
    private String roomType;
    private Integer capacity;
    private String facilities; // CSV string

    public RoomSearchResponseDto() {}

    public RoomSearchResponseDto(Long roomId, String roomCode, String roomType, Integer capacity, String facilities) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomType = roomType;
        this.capacity = capacity;
        this.facilities = facilities;
    }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
}