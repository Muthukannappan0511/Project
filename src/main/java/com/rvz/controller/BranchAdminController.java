package com.rvz.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.BranchAdminDto.UsageLogRequest;
import com.rvz.entity.Room;
import com.rvz.entity.UsageLog;
import com.rvz.service.BranchAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/branch")
public class BranchAdminController {

	private final BranchAdminService service;

	public BranchAdminController(BranchAdminService service) {
		this.service = service;
	}

	@GetMapping("/rooms")
	public List<Room> listRooms() {
		return service.listRooms();
	}

	@PostMapping("/logs")
	public UsageLog addLog(@Valid @RequestBody UsageLogRequest req) {
		return service.addUsageLog(req);
	}
}