package com.rvz.controller;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.SuperAdminRecentLogsDto;
import com.rvz.service.SuperAdminRecentLogsService;

@RestController
@RequestMapping("/api/super/dashboard")
public class SuperAdminRecentLogsController {

    private final SuperAdminRecentLogsService service;

    public SuperAdminRecentLogsController(SuperAdminRecentLogsService service) {
        this.service = service;
    }
    @GetMapping("/recent-logs")
    public List<SuperAdminRecentLogsDto.BranchLogs> recentLogs() {
        return service.getRecentLogs();
    }

    // ✅ Today's logs (branch-wise)
    @GetMapping("/today-logs")
    public List<SuperAdminRecentLogsDto.BranchLogs> todayLogs() {
        return service.getTodayLogs();
    }
}

