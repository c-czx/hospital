package com.hospital;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "patient123";
        String encodedPassword = encoder.encode(password);
        System.out.println("Encoded password for '" + password + "': " + encodedPassword);
        
        // Test if the password matches
        boolean matches = encoder.matches(password, "$2a$10$TABKgNEhhCIZsR2ACeNzr.2VY.4HzBPVTZ/P2ESOmdC45iRHaJQAu");
        System.out.println("Password matches: " + matches);
    }
}