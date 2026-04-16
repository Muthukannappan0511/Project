package com.rvz.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.branchadmin.BookingDecisionRequestDto;
import com.rvz.entity.BookingRequest;
import com.rvz.service.AnalyticsService;
import com.rvz.service.ApprovalService;

@RestController
@RequestMapping("/api/branch/bookings")
public class BranchAdminBookingController {

	private final ApprovalService approvalService;
	private final AnalyticsService analyticsService;

	public BranchAdminBookingController(ApprovalService approvalService, AnalyticsService analyticsService) {
		this.approvalService = approvalService;
		this.analyticsService = analyticsService;
	}

	@GetMapping("/pending")
	public List<BookingRequest> pending(Authentication auth) {
		Long branchId = analyticsService.currentBranchId();
		return approvalService.pendingForBranch(branchId);
	}

	@PutMapping("/{id}/approve")
	public String approve(@PathVariable Long id, @RequestBody BookingDecisionRequestDto dto, Authentication auth) {
		approvalService.approve(auth.getName(), id, dto != null ? dto.getComment() : null);
		return "Approved";
	}

	@PutMapping("/{id}/reject")
	public String reject(@PathVariable Long id, @RequestBody BookingDecisionRequestDto dto, Authentication auth) {
		approvalService.reject(auth.getName(), id, dto != null ? dto.getComment() : null);
		return "Rejected";
	}
}
