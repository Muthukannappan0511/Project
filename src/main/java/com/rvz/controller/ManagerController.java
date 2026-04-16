package com.rvz.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.AnalyticsDto.DashboardResponse;
import com.rvz.dto.ManagerLogDto;
import com.rvz.dto.ManagerReportDto;
import com.rvz.dto.RoomDto;
import com.rvz.entity.UsageLog;
import com.rvz.service.AnalyticsService;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

	private final AnalyticsService analyticsService;

	public ManagerController(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	@GetMapping("/rooms")
	public List<RoomDto> getManagerRooms() {
		return analyticsService.getManagerRooms();
	}

	@GetMapping("/dashboard")
	public DashboardResponse dashboard(@RequestParam String from, @RequestParam String to,
			@RequestParam(required = false) Long roomId) {
		return analyticsService.managerDashboard(LocalDate.parse(from), LocalDate.parse(to), roomId);
	}

	@GetMapping("/report")
	public ManagerReportDto report(@RequestParam String from, @RequestParam String to) {
		return analyticsService.managerReport(LocalDate.parse(from), LocalDate.parse(to));
	}

	@GetMapping("/logs")
	public List<ManagerLogDto> getManagerLogs() {
		return analyticsService.getManagerLogs();
	}

	@GetMapping(value = "/report.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ResponseEntity<byte[]> downloadReportXlsx(@RequestParam String from, @RequestParam String to)
			throws Exception {

		LocalDate fromDate = LocalDate.parse(from);
		LocalDate toDate = LocalDate.parse(to);

		ManagerReportDto report = analyticsService.managerReport(fromDate, toDate);

		byte[] fileBytes;
		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			CellStyle headerStyle = wb.createCellStyle();
			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);

			Sheet summary = wb.createSheet("Summary");
			int r = 0;

			Row row0 = summary.createRow(r++);
			row0.createCell(0).setCellValue("From");
			row0.createCell(1).setCellValue(from);

			Row row1 = summary.createRow(r++);
			row1.createCell(0).setCellValue("To");
			row1.createCell(1).setCellValue(to);

			r++; // empty row

			String mostUsed = (report.getInsights() != null) ? report.getInsights().getMostUsedSpace() : "No Data";

			String leastUsed = (report.getInsights() != null) ? report.getInsights().getLeastUsedSpace() : "No Data";

			String peakTime = (report.getInsights() != null) ? report.getInsights().getPeakUsageTime() : "No Data";

			Row row2 = summary.createRow(r++);
			row2.createCell(0).setCellValue("Most Used Space");
			row2.createCell(1).setCellValue(mostUsed);

			Row row3 = summary.createRow(r++);
			row3.createCell(0).setCellValue("Least Used Space");
			row3.createCell(1).setCellValue(leastUsed);

			Row row4 = summary.createRow(r++);
			row4.createCell(0).setCellValue("Peak Usage Time");
			row4.createCell(1).setCellValue(peakTime);

			summary.autoSizeColumn(0);
			summary.autoSizeColumn(1);

			// =======================
			// Sheet 2: Utilization
			// =======================
			Sheet util = wb.createSheet("Utilization");
			int ur = 0;

			Row utilHeader = util.createRow(ur++);
			Cell u0 = utilHeader.createCell(0);
			Cell u1 = utilHeader.createCell(1);
			Cell u2 = utilHeader.createCell(2);

			u0.setCellValue("Space Name");
			u1.setCellValue("Type");
			u2.setCellValue("Utilization (%)");

			u0.setCellStyle(headerStyle);
			u1.setCellStyle(headerStyle);
			u2.setCellStyle(headerStyle);

			if (report.getRows() != null) {
				for (ManagerReportDto.SpaceUtilizationRow row : report.getRows()) {
					Row rr = util.createRow(ur++);
					rr.createCell(0).setCellValue(row.getSpaceName());
					rr.createCell(1).setCellValue(row.getType());
					rr.createCell(2).setCellValue(row.getUtilization());
				}
			}

			util.autoSizeColumn(0);
			util.autoSizeColumn(1);
			util.autoSizeColumn(2);

			Sheet logsSheet = wb.createSheet("Logs");
			int lr = 0;

			Row logsHeader = logsSheet.createRow(lr++);
			Cell lh0 = logsHeader.createCell(0);
			Cell lh1 = logsHeader.createCell(1);
			Cell lh2 = logsHeader.createCell(2);
			Cell lh3 = logsHeader.createCell(3);
			Cell lh4 = logsHeader.createCell(4);
			Cell lh5 = logsHeader.createCell(5);

			lh0.setCellValue("Room");
			lh1.setCellValue("Date");
			lh2.setCellValue("Start Time");
			lh3.setCellValue("End Time");
			lh4.setCellValue("Occupancy");
			lh5.setCellValue("Created By");

			lh0.setCellStyle(headerStyle);
			lh1.setCellStyle(headerStyle);
			lh2.setCellStyle(headerStyle);
			lh3.setCellStyle(headerStyle);
			lh4.setCellStyle(headerStyle);
			lh5.setCellStyle(headerStyle);

			List<UsageLog> logs = analyticsService.getManagerLogsInRange(fromDate, toDate);

			DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

			for (UsageLog l : logs) {
				Row rr = logsSheet.createRow(lr++);

				String roomCode = (l.getRoom() != null) ? l.getRoom().getRoomCode() : "N/A";
				String createdBy = (l.getCreatedBy() != null) ? l.getCreatedBy().getEmail() : "N/A";

				rr.createCell(0).setCellValue(roomCode);

				if (l.getStartTime() != null) {
					rr.createCell(1).setCellValue(l.getStartTime().toLocalDate().format(dateFmt));
					rr.createCell(2).setCellValue(l.getStartTime().toLocalTime().format(timeFmt));
				} else {
					rr.createCell(1).setCellValue("N/A");
					rr.createCell(2).setCellValue("N/A");
				}

				if (l.getEndTime() != null) {
					rr.createCell(3).setCellValue(l.getEndTime().toLocalTime().format(timeFmt));
				} else {
					rr.createCell(3).setCellValue("N/A");
				}

				rr.createCell(4).setCellValue(l.getOccupancyCount() != null ? l.getOccupancyCount() : 0);
				rr.createCell(5).setCellValue(createdBy);
			}

			for (int i = 0; i <= 5; i++) {
				logsSheet.autoSizeColumn(i);
			}
			wb.write(bos);
			fileBytes = bos.toByteArray();
		}

		String filename = "manager_report_" + from + "_to_" + to + ".xlsx";

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(fileBytes);
	}
}