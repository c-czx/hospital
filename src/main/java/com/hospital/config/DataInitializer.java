package com.hospital.config;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数据初始化器
 * 在应用启动时初始化基础数据
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final PasswordEncoder passwordEncoder;
    private final NurseService nurseService;
    
    public DataInitializer(UserService userService, DepartmentService departmentService, 
                         DoctorService doctorService, PasswordEncoder passwordEncoder,
                         NurseService nurseService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.doctorService = doctorService;
        this.passwordEncoder = passwordEncoder;
        this.nurseService = nurseService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        initializeDepartments();
        initializeUsers();
        initializeDoctors();
        initializeNurses();
    }
    
    /**
     * 初始化用户数据
     */
    private void initializeUsers() {
        if (userService.findByPhone("13800000000") == null) {
            User admin = new User();
            admin.setPassword("admin123");  // 不要预先加密，saveUser 方法会加密
            admin.setName("系统管理员");
            admin.setRole("ADMIN");
            admin.setGender("male");
            admin.setPhone("13800000000");
            admin.setEmail("admin@hospital.com");
            userService.saveUser(admin);
        }
        
        if (userService.findByPhone("13800000001") == null) {
            User doctor1 = new User();
            doctor1.setPassword("doctor123");  // 不要预先加密，saveUser 方法会加密
            doctor1.setName("张医生");
            doctor1.setRole("DOCTOR");
            doctor1.setGender("male");
            doctor1.setPhone("13800000001");
            doctor1.setEmail("doctor1@hospital.com");
            userService.saveUser(doctor1);
        }
        
        if (userService.findByPhone("13800000002") == null) {
            User nurse1 = new User();
            nurse1.setPassword("nurse123");  // 不要预先加密，saveUser 方法会加密
            nurse1.setName("李护士");
            nurse1.setRole("NURSE");
            nurse1.setGender("female");
            nurse1.setPhone("13800000002");
            nurse1.setEmail("nurse1@hospital.com");
            userService.saveUser(nurse1);
        }
        
        if (userService.findByPhone("13800000003") == null) {
            User patient1 = new User();
            patient1.setPassword("patient123");  // 不要预先加密，saveUser 方法会加密
            patient1.setName("王患者");
            patient1.setRole("PATIENT");
            patient1.setGender("male");
            patient1.setPhone("13800000003");
            patient1.setEmail("patient1@hospital.com");
            userService.saveUser(patient1);
        }
    }
    
    /**
     * 初始化科室数据 - 统一科室命名
     */
    private void initializeDepartments() {
        // 全科
        if (departmentService.findByNameContaining("全科").isEmpty()) {
            Department generalPractice = new Department();
            generalPractice.setName("全科");
            generalPractice.setDescription("全科医疗诊疗");
            departmentService.saveDepartment(generalPractice);
            System.out.println("【创建科室】全科");
        } else {
            System.out.println("【已存在】全科");
        }
        
        // 内科
        if (departmentService.findByNameContaining("内科").isEmpty()) {
            Department internalMedicine = new Department();
            internalMedicine.setName("内科");
            internalMedicine.setDescription("内科疾病诊疗");
            departmentService.saveDepartment(internalMedicine);
            System.out.println("【创建科室】内科");
        } else {
            System.out.println("【已存在】内科");
        }
        
        // 外科
        if (departmentService.findByNameContaining("外科").isEmpty()) {
            Department surgery = new Department();
            surgery.setName("外科");
            surgery.setDescription("外科手术及治疗");
            departmentService.saveDepartment(surgery);
            System.out.println("【创建科室】外科");
        } else {
            System.out.println("【已存在】外科");
        }
        
        // 耳鼻喉
        if (departmentService.findByNameContaining("耳鼻喉").isEmpty()) {
            Department ent = new Department();
            ent.setName("耳鼻喉");
            ent.setDescription("耳鼻喉科疾病诊疗");
            departmentService.saveDepartment(ent);
            System.out.println("【创建科室】耳鼻喉");
        } else {
            System.out.println("【已存在】耳鼻喉");
        }
        
        // 妇产科
        if (departmentService.findByNameContaining("妇产科").isEmpty()) {
            Department gynecology = new Department();
            gynecology.setName("妇产科");
            gynecology.setDescription("妇科产科疾病诊疗");
            departmentService.saveDepartment(gynecology);
            System.out.println("【创建科室】妇产科");
        } else {
            System.out.println("【已存在】妇产科");
        }
        
        System.out.println("========================================");
        System.out.println("【科室初始化完成】共5个科室：全科、内科、外科、耳鼻喉、妇产科");
        System.out.println("========================================");
    }
    
    /**
     * 初始化医生数据
     */
    private void initializeDoctors() {
        System.out.println("========================================");
        System.out.println("【初始化医生数据】开始检查...");
        
        java.util.List<User> doctorUsers = userService.findByRole("DOCTOR");
        System.out.println("【医生用户数量】" + doctorUsers.size());
        
        for (User user : doctorUsers) {
            Doctor existingDoctor = doctorService.findByUserId(user.getId());
            if (existingDoctor == null) {
                System.out.println("【创建医生记录】用户：" + user.getName() + ", ID: " + user.getId());
                
                // 默认使用全科作为初始科室
                Department defaultDept = departmentService.findByNameContaining("全科").get(0);
                
                Doctor doctor = new Doctor();
                doctor.setUser(user);
                doctor.setDepartment(defaultDept);
                doctor.setTitle("主任医师");
                doctor.setSpecialty("全科");
                doctor.setSchedule("周一至周五 8:00-12:00, 14:00-17:00");
                doctorService.saveDoctor(doctor);
                
                System.out.println("【创建成功】医生 ID: " + doctor.getId() + ", 科室: 全科");
            } else {
                System.out.println("【已存在】用户：" + user.getName() + ", 医生 ID: " + existingDoctor.getId());
            }
        }
        
        System.out.println("========================================");
    }
    
    /**
     * 初始化护士数据
     */
    private void initializeNurses() {
        System.out.println("========================================");
        System.out.println("【初始化护士数据】开始检查...");
        
        List<User> nurseUsers = userService.findByRole("NURSE");
        System.out.println("【护士用户数量】" + nurseUsers.size());
        
        for (User user : nurseUsers) {
            // 检查是否已存在护士记录
            boolean exists = false;
            List<Nurse> existingNurses = nurseService.findAll();
            for (Nurse nurse : existingNurses) {
                if (nurse.getUser() != null && nurse.getUser().getId().equals(user.getId())) {
                    exists = true;
                    System.out.println("【已存在】用户：" + user.getName() + ", 护士 ID: " + nurse.getId());
                    break;
                }
            }
            
            if (!exists) {
                System.out.println("【创建护士记录】用户：" + user.getName() + ", ID: " + user.getId());
                
                Nurse nurse = new Nurse();
                nurse.setUser(user);
                nurse.setPhone(user.getPhone());
                nurseService.saveNurse(nurse);
                
                System.out.println("【创建成功】护士 ID: " + nurse.getId());
            }
        }
        
        System.out.println("========================================");
        System.out.println("【护士初始化完成】");
        System.out.println("========================================");
    }
}

