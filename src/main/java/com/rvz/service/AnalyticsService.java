package com.rvz.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rvz.dto.AnalyticsDto.ChartData;
import com.rvz.dto.AnalyticsDto.DashboardResponse;
import com.rvz.dto.AnalyticsDto.SummaryData;
import com.rvz.dto.ManagerLogDto;
import com.rvz.dto.ManagerReportDto;
import com.rvz.dto.RoomDto;
import com.rvz.entity.AppUser;
import com.rvz.entity.Room;
import com.rvz.entity.UsageLog;
import com.rvz.exception.NotFoundException;
import com.rvz.repo.RoomRepo;
import com.rvz.repo.UsageLogRepo;
import com.rvz.repo.UserRepo;
import com.rvz.util.SecurityUtil;

@Service
public class AnalyticsService {

	private final UserRepo userRepo;
	private final RoomRepo roomRepo;
	private final UsageLogRepo logRepo;

	public AnalyticsService(UserRepo userRepo, RoomRepo roomRepo, UsageLogRepo logRepo) {
		this.userRepo = userRepo;
		this.roomRepo = roomRepo;
		this.logRepo = logRepo;
	}

	public Long currentBranchId() {
	    String email = SecurityUtil.currentEmail();

	    AppUser user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (user.getBranch() == null) {
	        throw new RuntimeException("Branch not assigned to this user");
	    }

	    return user.getBranch().getBranchId();
	}

	public DashboardResponse managerDashboard(LocalDate from, LocalDate to, Long roomId) {

		// ✅ Branch of logged-in manager
		Long branchId = currentBranchId();

		// ✅ Rooms in branch
		List<Room> rooms = roomRepo.findByBranch_BranchId(branchId);
		if (rooms == null) {
			rooms = List.of();
		}

		// ✅ Date range
		LocalDateTime fromDt = from.atStartOfDay();
		LocalDateTime toDt = to.plusDays(1).atStartOfDay();

		// ✅ Logs in range
		List<UsageLog> logs = logRepo.findByRoom_Branch_BranchIdAndStartTimeBetween(branchId, fromDt, toDt);

		// =====================================================
		// ✅ SUMMARY DATA (TOP CARDS)
		// =====================================================
		long roomsCount = rooms.size();
		long logsCount = logs.size();

		long usageHours = logs.stream().mapToLong(l -> Duration.between(l.getStartTime(), l.getEndTime()).toHours())
				.sum();

		String peakTime = calculatePeakTime(logs);

		SummaryData summary = new SummaryData(roomsCount, logsCount, usageHours, peakTime);

		// =====================================================
		// ✅ ROOM‑BASED 7‑DAY TREND CHART
		// X‑axis = Days
		// Y‑axis = Usage HOURS
		// =====================================================
		List<String> labels = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		// ✅ Determine selected room ONCE (effectively final)
		Room selectedRoom;

		if (roomId != null) {
			selectedRoom = rooms.stream().filter(r -> r.getRoomId() != null && r.getRoomId().equals(roomId)).findFirst()
					.orElse(null);
		} else {
			selectedRoom = rooms.isEmpty() ? null : rooms.get(0);
		}

		// ✅ Last 7 days
		for (int i = 6; i >= 0; i--) {

			LocalDate day = LocalDate.now().minusDays(i);
			labels.add(day.getDayOfWeek().name().substring(0, 3) // MON, TUE...
			);

			if (selectedRoom == null) {
				values.add(0.0);
				continue;
			}

			long minutes = logs.stream().filter(l -> l.getRoom().getRoomId().equals(selectedRoom.getRoomId()))
					.filter(l -> l.getStartTime().toLocalDate().equals(day))
					.mapToLong(l -> Duration.between(l.getStartTime(), l.getEndTime()).toMinutes()).sum();

			double hours = Math.round((minutes / 60.0) * 10.0) / 10.0;

			values.add(hours);
		}

		ChartData chart = new ChartData(labels, values);

		// =====================================================
		// ✅ FINAL RESPONSE
		// =====================================================
		return new DashboardResponse(summary, chart);
	}

	public List<RoomDto> getManagerRooms() {

		String email = SecurityUtil.currentEmail();

		AppUser user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Long branchId = user.getBranch().getBranchId();

		List<Room> rooms = roomRepo.findByBranch_BranchId(branchId);

		List<RoomDto> result = new ArrayList<>();

		for (Room r : rooms) {
			RoomDto dto = new RoomDto();
			dto.setRoomId(r.getRoomId());
			dto.setRoomCode(r.getRoomCode());
			dto.setRoomType(r.getRoomType().name());
			dto.setCapacity(r.getCapacity());

			result.add(dto);
		}

		return result;
	}

	private String calculatePeakTime(List<UsageLog> logs) {

		if (logs == null || logs.isEmpty()) {
			return "No Data";
		}

		Map<Integer, Integer> hourUsageCount = new HashMap<>();

		for (UsageLog log : logs) {

			if (log.getStartTime() == null || log.getEndTime() == null) {
				continue;
			}

			int startHour = log.getStartTime().getHour();
			int endHour = log.getEndTime().getHour();

			// If a log is within same hour (e.g., 10:15 to 10:45), count that hour
			if (startHour == endHour) {
				hourUsageCount.put(startHour, hourUsageCount.getOrDefault(startHour, 0) + 1);
				continue;
			}

			for (int hour = startHour; hour < endHour; hour++) {
				hourUsageCount.put(hour, hourUsageCount.getOrDefault(hour, 0) + 1);
			}
		}

		if (hourUsageCount.isEmpty()) {
			return "No Data";
		}

		int peakHour = -1;
		int maxUsage = 0;

		for (Map.Entry<Integer, Integer> entry : hourUsageCount.entrySet()) {
			if (entry.getValue() > maxUsage) {
				maxUsage = entry.getValue();
				peakHour = entry.getKey();
			}
		}

		if (peakHour < 0) {
			return "No Data";
		}

		return String.format("%02d:00 - %02d:00", peakHour, peakHour + 1);
	}

	public List<ManagerLogDto> getManagerLogs() {

		String email = SecurityUtil.currentEmail();

		AppUser user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Long branchId = user.getBranch().getBranchId();

		List<UsageLog> logs = logRepo.findByRoom_Branch_BranchId(branchId);

		List<ManagerLogDto> result = new ArrayList<>();
		for (UsageLog l : logs) {
			ManagerLogDto dto = new ManagerLogDto();
			dto.setLogId(l.getLogId());
			dto.setRoomCode(l.getRoom().getRoomCode());
			dto.setStartTime(l.getStartTime());
			dto.setEndTime(l.getEndTime());
			dto.setOccupancyCount(l.getOccupancyCount());
			result.add(dto);
		}

		return result;
	}

	public ManagerReportDto managerReport(LocalDate from, LocalDate to) {

		Long branchId = currentBranchId();

		// ✅ Fetch rooms
		List<Room> rooms = roomRepo.findByBranch_BranchId(branchId);
		if (rooms == null) {
			rooms = List.of();
		}

		// ✅ Date range
		LocalDateTime fromDt = from.atStartOfDay();
		LocalDateTime toDt = to.plusDays(1).atStartOfDay();

		// ✅ Fetch logs
		List<UsageLog> logs = logRepo.findByRoom_Branch_BranchIdAndStartTimeBetween(branchId, fromDt, toDt);

		// ✅ Total days count
		long days = Duration.between(from.atStartOfDay(), to.plusDays(1).atStartOfDay()).toDays();

		List<ManagerReportDto.SpaceUtilizationRow> rows = new ArrayList<>();

		// ✅ Calculate utilization per room
		for (Room r : rooms) {

			long availablePerDayMin = Duration.between(r.getAvailableFrom(), r.getAvailableTo()).toMinutes();

			long availableTotalMin = availablePerDayMin * days;

			long occupiedMin = logs.stream().filter(l -> l.getRoom().getRoomId().equals(r.getRoomId()))
					.mapToLong(l -> Duration.between(l.getStartTime(), l.getEndTime()).toMinutes()).sum();

			double pct = (availableTotalMin == 0) ? 0.0 : (occupiedMin * 100.0 / availableTotalMin);

			pct = Math.round(pct * 10.0) / 10.0;

			rows.add(new ManagerReportDto.SpaceUtilizationRow(r.getRoomId(), r.getRoomCode(), r.getRoomType().name(),
					pct));
		}

		// ✅ Insights: most / least used space
		String mostUsed = rows.isEmpty() ? "No Data"
				: rows.stream().max(Comparator.comparingDouble(ManagerReportDto.SpaceUtilizationRow::getUtilization))
						.get().getSpaceName();

		String leastUsed = rows.isEmpty() ? "No Data"
				: rows.stream().min(Comparator.comparingDouble(ManagerReportDto.SpaceUtilizationRow::getUtilization))
						.get().getSpaceName();

		// ✅ Peak usage time
		String peak = calculatePeakTime(logs);

		ManagerReportDto.KeyInsights insights = new ManagerReportDto.KeyInsights(mostUsed, leastUsed, peak);

		return new ManagerReportDto(rows, insights);
	}
	public List<UsageLog> getManagerLogsInRange(LocalDate from, LocalDate to) {

	    Long branchId = currentBranchId();

	    LocalDateTime fromDt = from.atStartOfDay();
	    LocalDateTime toDt = to.plusDays(1).atStartOfDay();

	    return logRepo.findByRoom_Branch_BranchIdAndStartTimeBetween(branchId, fromDt, toDt);
	}
}