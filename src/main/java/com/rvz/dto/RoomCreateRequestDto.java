package com.rvz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomCreateRequestDto {

    @NotBlank
    private String roomCode;

    @NotBlank
    private String roomType;   // "LAB", "CLASSROOM", etc.

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotBlank
    private String availableFrom; // "09:00" or "09:00:00"

    @NotBlank
    private String availableTo;   // "17:00" or "17:00:00"

    private String facilities;    // "Projector,AC,Whiteboard"
    private Boolean active;       // optional (default true)

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }

    public String getAvailableTo() { return availableTo; }
    public void setAvailableTo(String availableTo) { this.availableTo = availableTo; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
