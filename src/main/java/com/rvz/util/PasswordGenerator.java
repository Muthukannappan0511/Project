package com.rvz.util;


import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#";
    private static final SecureRandom R = new SecureRandom();

    public static String generate(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(R.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}