package com.rvz.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rvz.dto.SuperAdminReportDto;
import com.rvz.entity.Branch;
import com.rvz.entity.Room;
import com.rvz.entity.UsageLog;
import com.rvz.repo.BranchRepo;
import com.rvz.repo.RoomRepo;
import com.rvz.repo.UsageLogRepo;

@Service
public class SuperAdminReportService {

    private final BranchRepo branchRepo;
    private final RoomRepo roomRepo;
    private final UsageLogRepo logRepo;

    public SuperAdminReportService(
            BranchRepo branchRepo,
            RoomRepo roomRepo,
            UsageLogRepo logRepo
    ) {
        this.branchRepo = branchRepo;
        this.roomRepo = roomRepo;
        this.logRepo = logRepo;
    }

    /* -------------------- Main Report Method -------------------- */
    public SuperAdminReportDto usageReport(
            LocalDate from,
            LocalDate to,
            Long branchId
    ) {

        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();

        List<Branch> branches = branchRepo.findAll();
        List<UsageLog> logs = logRepo.findByStartTimeBetween(fromDt, toDt);

        /* -------------------- Group logs by Branch -------------------- */
        Map<Long, List<UsageLog>> logsByBranch = logs.stream()
                .filter(l -> l.getRoom() != null && l.getRoom().getBranch() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getRoom().getBranch().getBranchId()
                ));

        /* -------------------- Usage Hours & Log Count -------------------- */
        Map<Long, Long> hoursByBranch = new HashMap<>();
        Map<Long, Long> countByBranch = new HashMap<>();

        for (Map.Entry<Long, List<UsageLog>> entry : logsByBranch.entrySet()) {
            long hrs = entry.getValue().stream()
                    .mapToLong(l ->
                            Duration
                                    .between(l.getStartTime(), l.getEndTime())
                                    .toHours()
                    )
                    .sum();

            hoursByBranch.put(entry.getKey(), hrs);
            countByBranch.put(entry.getKey(), (long) entry.getValue().size());
        }

        long totalHoursAllBranches = hoursByBranch.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();

        /* -------------------- Pie Chart Data -------------------- */
        List<SuperAdminReportDto.BranchSlice> slices = new ArrayList<>();

        for (Branch b : branches) {
            long hrs = hoursByBranch.getOrDefault(b.getBranchId(), 0L);
            long cnt = countByBranch.getOrDefault(b.getBranchId(), 0L);

            double pct = (totalHoursAllBranches == 0)
                    ? 0.0
                    : (hrs * 100.0 / totalHoursAllBranches);

            pct = Math.round(pct * 10.0) / 10.0; // 1 decimal rounding

            slices.add(new SuperAdminReportDto.BranchSlice(
                    b.getBranchId(),
                    b.getBranchName(),
                    hrs,
                    cnt,
                    pct
            ));
        }

        /* -------------------- Selected Branch -------------------- */
        Long selectedBranchId = branchId;

        if (selectedBranchId == null) {
            selectedBranchId = slices.stream()
                    .max(Comparator.comparingLong(
                            SuperAdminReportDto.BranchSlice::getUsageHours
                    ))
                    .map(SuperAdminReportDto.BranchSlice::getBranchId)
                    .orElse(null);
        }

        SuperAdminReportDto.BranchInsights insights =
                buildBranchInsights(selectedBranchId, logsByBranch, branches);

        return new SuperAdminReportDto(slices, insights);
    }

    /* -------------------- Branch Insights -------------------- */
    private SuperAdminReportDto.BranchInsights buildBranchInsights(
            Long branchId,
            Map<Long, List<UsageLog>> logsByBranch,
            List<Branch> branches
    ) {

        if (branchId == null) {
            return new SuperAdminReportDto.BranchInsights(
                    null,
                    "No Data",
                    "No Data",
                    "No Data",
                    "No Data",
                    0,
                    0
            );
        }

        String branchName = branches.stream()
                .filter(b -> b.getBranchId().equals(branchId))
                .map(Branch::getBranchName)
                .findFirst()
                .orElse("Unknown");

        List<UsageLog> branchLogs =
                logsByBranch.getOrDefault(branchId, Collections.emptyList());

        long logsCount = branchLogs.size();

        long usageHours = branchLogs.stream()
                .mapToLong(l ->
                        Duration
                                .between(l.getStartTime(), l.getEndTime())
                                .toHours()
                )
                .sum();

        String peakTime = calculatePeakTime(branchLogs);

        /* -------------------- Room Usage -------------------- */
        Map<String, Long> roomMinutes = new HashMap<>();

        for (UsageLog log : branchLogs) {
            Room room = log.getRoom();
            if (room == null) continue;

            String roomCode = room.getRoomCode();
            long mins = Duration
                    .between(log.getStartTime(), log.getEndTime())
                    .toMinutes();

            roomMinutes.put(
                    roomCode,
                    roomMinutes.getOrDefault(roomCode, 0L) + mins
            );
        }

        String mostUsedRoom = "No Data";
        String leastUsedRoom = "No Data";

        if (!roomMinutes.isEmpty()) {
            mostUsedRoom = roomMinutes.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();

            leastUsedRoom = roomMinutes.entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue())
                    .get()
                    .getKey();
        }

        return new SuperAdminReportDto.BranchInsights(
                branchId,
                branchName,
                peakTime,
                mostUsedRoom,
                leastUsedRoom,
                logsCount,
                usageHours
        );
    }

    /* -------------------- Peak Usage Time -------------------- */
    private String calculatePeakTime(List<UsageLog> logs) {

        if (logs == null || logs.isEmpty()) {
            return "No Data";
        }

        Map<Integer, Integer> hourCount = new HashMap<>();

        for (UsageLog log : logs) {
            if (log.getStartTime() == null || log.getEndTime() == null) continue;

            int startHour = log.getStartTime().getHour();
            int endHour = log.getEndTime().getHour();

            if (startHour == endHour) {
                hourCount.put(startHour,
                        hourCount.getOrDefault(startHour, 0) + 1);
                continue;
            }

            for (int h = startHour; h < endHour; h++) {
                hourCount.put(h,
                        hourCount.getOrDefault(h, 0) + 1);
            }
        }

        int peakHour = -1;
        int maxCount = 0;

        for (Map.Entry<Integer, Integer> e : hourCount.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                peakHour = e.getKey();
            }
        }

        if (peakHour < 0) {
            return "No Data";
        }

        return String.format(
                "%02d:00 - %02d:00",
                peakHour,
                peakHour + 1
        );
    }
}