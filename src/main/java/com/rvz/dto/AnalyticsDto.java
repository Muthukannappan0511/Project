package com.rvz.dto;

import java.util.List;

public class AnalyticsDto {
    public record ChartData(List<String> labels, List<Double> values) {}
       public record SummaryData(
               long roomsCount,
               long logsCount,
               long usageHours,
               String peakTime
       ) {}
       public record DashboardResponse(
               SummaryData summary,
               ChartData chart
       ) {}

}