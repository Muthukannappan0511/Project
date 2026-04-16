package com.rvz.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rvz.dto.SuperAdminDto.BranchRequest;
import com.rvz.dto.SuperAdminDto.CreateUserRequest;
import com.rvz.dto.SuperAdminDto.UpdateUserRequest;
import com.rvz.dto.SuperAdminDto.UserResponse;
import com.rvz.entity.AppUser;
import com.rvz.entity.Branch;
import com.rvz.entity.Role;
import com.rvz.exception.BadRequestException;
import com.rvz.exception.NotFoundException;
import com.rvz.repo.BranchRepo;
import com.rvz.repo.UserRepo;
import com.rvz.util.PasswordGenerator;

@Service
public class SuperAdminService {

    private final BranchRepo branchRepo;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder;
    private final EmailService emailService;

    public SuperAdminService(BranchRepo branchRepo, UserRepo userRepo,
                             BCryptPasswordEncoder encoder, EmailService emailService) {
        this.branchRepo = branchRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    public void createBranch(BranchRequest req) {
        if (branchRepo.existsById(req.branchId())) throw new BadRequestException("branchId already exists");
        if (branchRepo.existsByBranchName(req.branchName())) throw new BadRequestException("branchName already exists");

        Branch b = new Branch(req.branchId(), req.branchName(), true);
        branchRepo.save(b);
    }

    public void createUser(CreateUserRequest req) {
        if (userRepo.existsByEmail(req.email())) throw new BadRequestException("Email already exists");

        Branch branch = null;
        if (req.role() != Role.SUPER_ADMIN) {
            if (req.branchId() == null) throw new BadRequestException("branchId required for this role");
            branch = branchRepo.findById(req.branchId())
                    .orElseThrow(() -> new NotFoundException("Branch not found"));
        }

        String tempPassword = PasswordGenerator.generate(10);

        AppUser u = new AppUser();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setRole(req.role());
        u.setBranch(branch);
        u.setPasswordHash(encoder.encode(tempPassword));  // encrypted in DB
        u.setEnabled(true);
        u.setMustChangePassword(true);

        try {
        	System.out.println(req.email());
            emailService.sendCredentials(req.email(), tempPassword);
            userRepo.save(u);
            
            }catch (Exception ex) {
            System.out.println("EMAIL FAILED for " + req.email() + " : " + ex.getMessage());
            }

    }

    public List<UserResponse> listUsers() {
        return userRepo.findAll().stream()
                .map(u -> new UserResponse(
                        u.getUserId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.getBranch() == null ? null : u.getBranch().getBranchId(),
                        u.isEnabled()
                )).collect(Collectors.toList());
    }

    public void updateUser(Long id, UpdateUserRequest req) {
        var user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        Branch branch = null;
        Role role = req.role();
        if (role != Role.SUPER_ADMIN) {
            if (req.branchId() == null) throw new BadRequestException("branchId required for this role");
            branch = branchRepo.findById(req.branchId()).orElseThrow(() -> new NotFoundException("Branch not found"));
        }

        user.setName(req.name());
        user.setRole(role);
        user.setBranch(branch);
        user.setEnabled(req.enabled());
        userRepo.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) throw new NotFoundException("User not found");
        userRepo.deleteById(id);
    }
}