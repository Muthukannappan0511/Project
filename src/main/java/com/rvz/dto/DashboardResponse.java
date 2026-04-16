package com.rvz.dto;

import java.util.List;

public class DashboardResponse {

	public static class Summary {
		public long roomsCount;
		public long logsCount;
		public long usageHours;
		public String peakTime;

		public Summary(long roomsCount, long logsCount, long usageHours, String peakTime) {
			this.roomsCount = roomsCount;
			this.logsCount = logsCount;
			this.usageHours = usageHours;
			this.peakTime = peakTime;
		}
	}

	public static class RoomItem {
		public Long roomId;
		public String roomCode;

		public RoomItem(Long roomId, String roomCode) {
			this.roomId = roomId;
			this.roomCode = roomCode;
		}
	}

	public static class Chart {
		public List<String> labels;
		public List<Long> values;

		public Chart(List<String> labels, List<Long> values) {
			this.labels = labels;
			this.values = values;
		}
	}

	public Summary summary;
	public List<RoomItem> rooms;
	public Chart chart;

	public DashboardResponse(Summary summary, List<RoomItem> rooms, Chart chart) {
		this.summary = summary;
		this.rooms = rooms;
		this.chart = chart;
	}
}