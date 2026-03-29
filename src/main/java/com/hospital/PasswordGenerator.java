package com.hospital;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成各角色的密码
        String adminPassword = encoder.encode("admin123");
        String doctorPassword = encoder.encode("doctor123");
        String nursePassword = encoder.encode("nurse123");
        String patientPassword = encoder.encode("patient123");
        
        System.out.println("Admin password: " + adminPassword);
        System.out.println("Doctor password: " + doctorPassword);
        System.out.println("Nurse password: " + nursePassword);
        System.out.println("Patient password: " + patientPassword);
    }
}