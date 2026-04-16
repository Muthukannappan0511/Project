package com.rvz.service;

import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.rvz.dto.RoomCreateRequestDto;
import com.rvz.entity.Branch;
import com.rvz.entity.Room;
import com.rvz.entity.RoomType;
import com.rvz.repo.BranchRepo;
import com.rvz.repo.RoomRepo;

@Service
public class RoomService {

    private final RoomRepo roomRepo;
    private final BranchRepo branchRepo;
    private final AnalyticsService analyticsService;

    public RoomService(
            RoomRepo roomRepo,
            BranchRepo branchRepo,
            AnalyticsService analyticsService
    ) {
        this.roomRepo = roomRepo;
        this.branchRepo = branchRepo;
        this.analyticsService = analyticsService;
    }

    public String createRoom(RoomCreateRequestDto dto) {

        if (dto == null) {
            throw new RuntimeException("Request body is required");
        }

        Long branchId = analyticsService.currentBranchId();

        // 1) Validate roomCode
        String roomCode = dto.getRoomCode() == null ? "" : dto.getRoomCode().trim();
        if (roomCode.isEmpty()) {
            throw new RuntimeException("Room code is required");
        }

        if (roomRepo.existsByBranch_BranchIdAndRoomCodeIgnoreCase(branchId, roomCode)) {
            throw new RuntimeException("Room code already exists in this branch");
        }

        // 2) Validate capacity
        if (dto.getCapacity() == null || dto.getCapacity() < 1) {
            throw new RuntimeException("Capacity must be at least 1");
        }

        // 3) Validate & parse times
        LocalTime from = parseTime(dto.getAvailableFrom());
        LocalTime to   = parseTime(dto.getAvailableTo());

        if (!from.isBefore(to)) {
            throw new RuntimeException("Available From must be before Available To");
        }

        // 4) Validate roomType
        if (dto.getRoomType() == null || dto.getRoomType().trim().isEmpty()) {
            throw new RuntimeException("Room type is required");
        }

        RoomType type;
        try {
            type = RoomType.valueOf(dto.getRoomType().trim());
        } catch (Exception ex) {
            throw new RuntimeException("Invalid room type");
        }

        // 5) Branch fetch
        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // 6) Normalize facilities CSV
        String facilities = normalizeFacilities(dto.getFacilities());

        // 7) Active default true
        boolean active = dto.getActive() == null || dto.getActive();

        // 8) Save room
        Room room = new Room();
        room.setRoomCode(roomCode);
        room.setRoomType(type);
        room.setCapacity(dto.getCapacity());
        room.setAvailableFrom(from);
        room.setAvailableTo(to);
        room.setFacilities(facilities);
        room.setActive(active);
        room.setBranch(branch);

        roomRepo.save(room);

        return "Room created successfully";
    }

    private LocalTime parseTime(String time) {
        if (time == null || time.trim().isEmpty()) {
            throw new RuntimeException("Available time is required");
        }

        String t = time.trim();

        // Accepts "09:00" or "09:00:00"
        if (t.length() == 5) {
            t = t + ":00";
        }

        return LocalTime.parse(t);
    }

    // Converts "Projector, AC ,Whiteboard" -> "Projector,AC,Whiteboard"
    private String normalizeFacilities(String facilities) {
        if (facilities == null) return null;

        String trimmed = facilities.trim();
        if (trimmed.isEmpty()) return null;

        String[] parts = trimmed.split(",");
        StringBuilder sb = new StringBuilder();

        for (String p : parts) {
            String v = p.trim();
            if (v.isEmpty()) continue;

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(v);
        }

        return sb.length() == 0 ? null : sb.toString();
    }
}