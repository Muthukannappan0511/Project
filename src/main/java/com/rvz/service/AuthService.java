package com.rvz.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rvz.dto.AuthDto.ChangePasswordRequest;
import com.rvz.dto.AuthDto.LoginRequest;
import com.rvz.dto.AuthDto.LoginResponse;
import com.rvz.exception.UnauthorizedException;
import com.rvz.repo.UserRepo;
import com.rvz.security.JwtUtil;
import com.rvz.util.SecurityUtil;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepo userRepo, BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest req) {

        var user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User disabled");
        }

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generate(user.getEmail(), user.getRole().name());
        Long branchId = (user.getBranch() == null) ? null : user.getBranch().getBranchId();

        return new LoginResponse(token, user.getRole().name(), branchId, user.isMustChangePassword());
    }

    public String changePassword(ChangePasswordRequest req) {

        if (!req.newPassword().equals(req.confirmPassword())) {
            throw new com.rvz.exception.BadRequestException("New password and confirm password do not match");
        }

        String email = com.rvz.util.SecurityUtil.currentEmail();

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new com.rvz.exception.UnauthorizedException("User not found"));
        user.setPasswordHash(encoder.encode(req.newPassword()));
        user.setMustChangePassword(false);
        userRepo.save(user);

        return "Password changed successfully";
    }
}