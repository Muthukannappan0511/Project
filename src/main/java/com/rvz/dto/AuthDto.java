package com.rvz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDto {
	public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record LoginResponse(String token, String role, Long branchId, boolean mustChangePassword) {}

    public record ChangePasswordRequest(
               @NotBlank String newPassword,
               @NotBlank String confirmPassword
    ) {}

}