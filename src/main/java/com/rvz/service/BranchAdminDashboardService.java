package com.rvz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rvz.dto.BranchAdminDashboardDto.BranchSummaryDto;
import com.rvz.dto.BranchAdminDashboardDto.MyLogDto;
import com.rvz.entity.AppUser;
import com.rvz.entity.UsageLog;
import com.rvz.repo.RoomRepo;
import com.rvz.repo.UsageLogRepo;
import com.rvz.repo.UserRepo;
import com.rvz.util.SecurityUtil;

@Service
public class BranchAdminDashboardService {

    private final UsageLogRepo usageLogRepo;
    private final RoomRepo roomRepo;
    private final UserRepo userRepo;

    public BranchAdminDashboardService(
            UsageLogRepo usageLogRepo,
            RoomRepo roomRepo,
            UserRepo userRepo) {

        this.usageLogRepo = usageLogRepo;
        this.roomRepo = roomRepo;
        this.userRepo = userRepo;
    }

    //My Logs
    public List<MyLogDto> getMyLogs() {

        AppUser currentUser = SecurityUtil.currentUser();

        List<UsageLog> logs =
                usageLogRepo.findByCreatedByOrderByStartTimeDesc(currentUser);

        return logs.stream()
                .map(log -> new MyLogDto(
                        log.getLogId(),
                        log.getRoom().getRoomCode(),
                        log.getStartTime(),
                        log.getEndTime(),
                        log.getOccupancyCount()
                ))
                .toList();
    }

    // Dashboard summary
    public BranchSummaryDto getBranchSummary() {

        AppUser currentUser = SecurityUtil.currentUser();
        Long branchId = currentUser.getBranch().getBranchId();

        long membersCount = userRepo.countByBranch_BranchId(branchId);
        long logsCount = usageLogRepo.countByRoom_Branch_BranchId(branchId);
        long roomsCount = roomRepo.countByBranch_BranchId(branchId);

        return new BranchSummaryDto(
                membersCount,
                logsCount,
                roomsCount
        );
    }
}
