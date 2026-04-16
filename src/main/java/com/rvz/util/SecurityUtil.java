package com.rvz.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.rvz.entity.AppUser;
import com.rvz.exception.UnauthorizedException;
import com.rvz.repo.UserRepo;

@Component
public class SecurityUtil {

    private static UserRepo userRepo;

    //  Inject UserRepo once
    public SecurityUtil(UserRepo userRepo) {
        SecurityUtil.userRepo = userRepo;
    }

    //  Existing method (keep it)
    public static String currentEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    //  NEW METHOD (this fixes your error)
    public static AppUser currentUser() {
        String email = currentEmail();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}