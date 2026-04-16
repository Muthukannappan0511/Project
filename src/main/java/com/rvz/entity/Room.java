package com.rvz.entity;
//
//import java.time.LocalTime;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "room")
//public class Room {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long roomId;
//
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "branch_id")
//    private Branch branch;
//
//    @Column(nullable = false)
//    private String roomCode;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private RoomType roomType;
//
//    @Column(nullable = false)
//    private int capacity;
//
//    @Column(nullable = false)
//    private LocalTime availableFrom;
//
//    @Column(nullable = false)
//    private LocalTime availableTo;
//
//    @Column(nullable = false)
//    private boolean active = true;
//
//    public Room() {
//    }
//
//    public Long getRoomId() {
//        return roomId;
//    }
//
//    public void setRoomId(Long roomId) {
//        this.roomId = roomId;
//    }
//
//    public Branch getBranch() {
//        return branch;
//    }
//
//    public void setBranch(Branch branch) {
//        this.branch = branch;
//    }
//
//    public String getRoomCode() {
//        return roomCode;
//    }
//
//    public void setRoomCode(String roomCode) {
//        this.roomCode = roomCode;
//    }
//
//    public RoomType getRoomType() {
//        return roomType;
//    }
//
//    public void setRoomType(RoomType roomType) {
//        this.roomType = roomType;
//    }
//
//    public int getCapacity() {
//        return capacity;
//    }
//
//    public void setCapacity(int capacity) {
//        this.capacity = capacity;
//    }
//
//    public LocalTime getAvailableFrom() {
//        return availableFrom;
//    }
//
//    public void setAvailableFrom(LocalTime availableFrom) {
//        this.availableFrom = availableFrom;
//    }
//
//    public LocalTime getAvailableTo() {
//        return availableTo;
//    }
//
//    public void setAvailableTo(LocalTime availableTo) {
//        this.availableTo = availableTo;
//    }
//    public boolean isActive() {
//        return active;
//    }
//
//    public void setActive(boolean active) {
//        this.active = active;
//    }
//    @Column(name = "facilities")
//    private String facilities;
//
//    public String getFacilities() {
//        return facilities;
//    }
//
//    public void setFacilities(String facilities) {
//        this.facilities = facilities;
//    }
//    
//   
//
//
//	
//}




// add imports if missing
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "available_from")
    private LocalTime availableFrom;

    @Column(name = "available_to")
    private LocalTime availableTo;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "facilities")
    private String facilities; // CSV string: "Projector,AC"

    @Column(name = "room_code")
    private String roomCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType roomType;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // ---------- getters/setters ----------
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalTime availableFrom) { this.availableFrom = availableFrom; }

    public LocalTime getAvailableTo() { return availableTo; }
    public void setAvailableTo(LocalTime availableTo) { this.availableTo = availableTo; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
}