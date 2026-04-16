package com.rvz.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptHashGenerator {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("Admin@123"));
    }
}
