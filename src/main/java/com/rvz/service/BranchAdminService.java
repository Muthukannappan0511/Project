package com.rvz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rvz.dto.BranchAdminDto.RoomRequest;
import com.rvz.dto.BranchAdminDto.UsageLogRequest;
import com.rvz.entity.AppUser;
import com.rvz.entity.Room;
import com.rvz.entity.UsageLog;
import com.rvz.exception.BadRequestException;
import com.rvz.exception.NotFoundException;
import com.rvz.repo.RoomRepo;
import com.rvz.repo.UsageLogRepo;
import com.rvz.repo.UserRepo;
import com.rvz.util.SecurityUtil;

@Service
public class BranchAdminService {

    private final UserRepo userRepo;
    private final RoomRepo roomRepo;
    private final UsageLogRepo logRepo;

    public BranchAdminService(UserRepo userRepo, RoomRepo roomRepo, UsageLogRepo logRepo) {
        this.userRepo = userRepo;
        this.roomRepo = roomRepo;
        this.logRepo = logRepo;
    }

    private AppUser currentUser() {
        return userRepo.findByEmail(SecurityUtil.currentEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Room addRoom(RoomRequest req) {
        AppUser user = currentUser();
        Long branchId = user.getBranch().getBranchId();

        if (req.availableTo().isBefore(req.availableFrom()) || req.availableTo().equals(req.availableFrom())) {
            throw new BadRequestException("availableTo must be greater than availableFrom");
        }

        if (roomRepo.existsByBranch_BranchIdAndRoomCode(branchId, req.roomCode())) {
            throw new BadRequestException("roomCode already exists in branch");
        }

        Room r = new Room();
        r.setBranch(user.getBranch());
        r.setRoomCode(req.roomCode());
        r.setRoomType(req.roomType());
        r.setCapacity(req.capacity());
        r.setAvailableFrom(req.availableFrom());
        r.setAvailableTo(req.availableTo());
        r.setActive(true);

        return roomRepo.save(r);
    }

    public List<Room> listRooms() {
        AppUser user = currentUser();
        return roomRepo.findByBranch_BranchId(user.getBranch().getBranchId());
    }

    public UsageLog addUsageLog(UsageLogRequest req) {
        AppUser user = currentUser();
        Long branchId = user.getBranch().getBranchId();

        if (!req.endTime().isAfter(req.startTime())) {
            throw new BadRequestException("endTime must be greater than startTime");
        }

        if (req.occupancyCount() != null && req.occupancyCount() < 0) {
            throw new BadRequestException("occupancy cannot be negative");
        }

        Room room = roomRepo.findById(req.roomId())
                .orElseThrow(() -> new NotFoundException("Room not found"));

        if (!room.getBranch().getBranchId().equals(branchId)) {
            throw new BadRequestException("Room not in your branch");
        }

        if (!room.getActive()) {
            throw new BadRequestException("Room inactive");
        }

        UsageLog log = new UsageLog();
        log.setRoom(room);
        log.setStartTime(req.startTime());
        log.setEndTime(req.endTime());
        log.setOccupancyCount(req.occupancyCount());
        log.setNotes(req.notes());
        log.setCreatedBy(user);

        return logRepo.save(log);
    }
}