package com.rvz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.BranchAdminDashboardDto.BranchSummaryDto;
import com.rvz.dto.BranchAdminDashboardDto.MyLogDto;
import com.rvz.service.BranchAdminDashboardService;

@RestController
@RequestMapping("/api/branch")
public class BranchAdminDashboardController {

    private final BranchAdminDashboardService service;

    public BranchAdminDashboardController(BranchAdminDashboardService service) {
        this.service = service;
    }

    // My Logs
    @GetMapping("/logs/my")
    public List<MyLogDto> myLogs() {
        return service.getMyLogs();
    }

    // Dashboard summary
    @GetMapping("/dashboard/summary")
    public BranchSummaryDto summary() {
        return service.getBranchSummary();
    }
}
