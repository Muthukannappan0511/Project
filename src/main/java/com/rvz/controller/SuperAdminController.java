package com.rvz.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.BranchListDto;
import com.rvz.dto.SuperAdminDashboardDto;
import com.rvz.dto.SuperAdminDto.BranchRequest;
import com.rvz.dto.SuperAdminDto.CreateUserRequest;
import com.rvz.dto.SuperAdminDto.UpdateUserRequest;
import com.rvz.dto.SuperAdminDto.UserResponse;
import com.rvz.dto.SuperAdminReportDto;
import com.rvz.repo.BranchRepo;
import com.rvz.repo.RoomRepo;
import com.rvz.repo.UserRepo;
import com.rvz.service.SuperAdminBranchService;
import com.rvz.service.SuperAdminReportService;
import com.rvz.service.SuperAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super")
public class SuperAdminController {

    private final BranchRepo branchRepo;
    private final UserRepo userRepo;
    private final RoomRepo roomRepo;

    private final SuperAdminService superAdminService;
    private final SuperAdminBranchService superAdminBranchService;
    private final SuperAdminReportService reportService;
    public SuperAdminController(
            BranchRepo branchRepo,
            UserRepo userRepo,
            RoomRepo roomRepo,
            SuperAdminService superAdminService,
            SuperAdminBranchService superAdminBranchService,
            SuperAdminReportService reportService
    ) {
        this.branchRepo = branchRepo;
        this.userRepo = userRepo;
        this.roomRepo = roomRepo;
        this.superAdminService = superAdminService;
        this.superAdminBranchService = superAdminBranchService;
        this.reportService = reportService;
    }

    // ------------------- Branches -------------------

    @PostMapping("/branches")
    public String createBranch(@Valid @RequestBody BranchRequest request) {
        superAdminService.createBranch(request);
        return "Branch created";
    }

    @GetMapping("/branches")
    public List<BranchListDto> listBranches() {
        return superAdminBranchService.getAllBranches();
    }

    // ------------------- Users -------------------

    @PostMapping("/users")
    public String createUser(@Valid @RequestBody CreateUserRequest request) {
        superAdminService.createUser(request);
        return "User created and credentials sent via email";
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return superAdminService.listUsers();
    }

    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        superAdminService.updateUser(id, request);
        return "User updated";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        superAdminService.deleteUser(id);
        return "User deleted";
    }

    // ------------------- Reports -------------------

    @GetMapping("/reports/usage")
    public SuperAdminReportDto usageReport(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) Long branchId
    ) {
        return reportService.usageReport(LocalDate.parse(from), LocalDate.parse(to), branchId);
    }

    // ------------------- Dashboard -------------------

    @GetMapping("/dashboard")
    public SuperAdminDashboardDto dashboard() {
        long branches = branchRepo.count();
        long users = userRepo.count();
        long rooms = roomRepo.count(); // ✅ Total rooms

        return new SuperAdminDashboardDto(branches, users, rooms);
    }
}

