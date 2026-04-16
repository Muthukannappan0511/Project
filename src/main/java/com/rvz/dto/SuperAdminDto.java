package com.rvz.dto;

import com.rvz.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SuperAdminDto {
    public record BranchRequest(@NotNull Long branchId, @NotBlank String branchName) {}

    public record CreateUserRequest(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotNull Role role,
            Long branchId
    ) {}

    public record UpdateUserRequest(
            @NotBlank String name,
            @NotNull Role role,
            Long branchId,
            boolean enabled
    ) {}

    public record UserResponse(Long userId, String name, String email, String role, Long branchId, boolean enabled) {}
}