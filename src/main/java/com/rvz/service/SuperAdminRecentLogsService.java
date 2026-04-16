package com.rvz.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rvz.dto.SuperAdminRecentLogsDto;
import com.rvz.entity.Branch;
import com.rvz.entity.UsageLog;
import com.rvz.repo.BranchRepo;
import com.rvz.repo.UsageLogRepo;

@Service
public class SuperAdminRecentLogsService {

    private final BranchRepo branchRepo;
    private final UsageLogRepo usageLogRepo;

    public SuperAdminRecentLogsService(BranchRepo branchRepo,
                                       UsageLogRepo usageLogRepo) {
        this.branchRepo = branchRepo;
        this.usageLogRepo = usageLogRepo;
    }

    // ✅ Latest logs per branch
    public List<SuperAdminRecentLogsDto.BranchLogs> getRecentLogs() {

        List<Branch> branches = branchRepo.findAll();
        List<SuperAdminRecentLogsDto.BranchLogs> response = new ArrayList<>();

        for (Branch branch : branches) {

            List<UsageLog> logs =
                    usageLogRepo.findTop5ByRoom_Branch_BranchIdOrderByStartTimeDesc(
                            branch.getBranchId()
                    );

            List<SuperAdminRecentLogsDto.RecentLog> logDtos = logs.stream()
                    .map(log -> new SuperAdminRecentLogsDto.RecentLog(
                            log.getRoom().getRoomCode(),
                            log.getStartTime(),
                            log.getEndTime(),
                            log.getOccupancyCount(),
                            log.getCreatedBy().getName()
                    ))
                    .toList();

            response.add(new SuperAdminRecentLogsDto.BranchLogs(
                    branch.getBranchId(),
                    branch.getBranchName(),
                    logDtos
            ));
        }

        return response;
    }

    // ✅ Today's logs per branch
    public List<SuperAdminRecentLogsDto.BranchLogs> getTodayLogs() {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Branch> branches = branchRepo.findAll();
        List<SuperAdminRecentLogsDto.BranchLogs> response = new ArrayList<>();

        for (Branch branch : branches) {

            List<UsageLog> logs =
                    usageLogRepo.findByRoom_Branch_BranchIdAndStartTimeBetweenOrderByStartTimeDesc(
                            branch.getBranchId(),
                            start,
                            end
                    );

            List<SuperAdminRecentLogsDto.RecentLog> logDtos = logs.stream()
                    .map(log -> new SuperAdminRecentLogsDto.RecentLog(
                            log.getRoom().getRoomCode(),
                            log.getStartTime(),
                            log.getEndTime(),
                            log.getOccupancyCount(),
                            log.getCreatedBy().getName()
                    ))
                    .toList();

            response.add(new SuperAdminRecentLogsDto.BranchLogs(
                    branch.getBranchId(),
                    branch.getBranchName(),
                    logDtos
            ));
        }

        return response;
    }
}