package com.hospital;

import com.hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Component
    public class PatientRecordInitializer implements ApplicationRunner {
        
        @Autowired
        private UserService userService;
        
        @Override
        public void run(ApplicationArguments args) throws Exception {
            System.out.println("\n========================================");
            System.out.println("【患者记录初始化】开始为已存在的患者用户创建患者记录...");
            userService.createPatientRecordsForExistingUsers();
            System.out.println("【患者记录初始化】完成");
            System.out.println("========================================\n");
        }
    }
}