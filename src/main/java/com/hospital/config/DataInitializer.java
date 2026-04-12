package com.hospital.config;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    private final PatientService patientService;
    
    public DataInitializer(UserService userService, DepartmentService departmentService, 
                         DoctorService doctorService, PasswordEncoder passwordEncoder,
                         NurseService nurseService, PatientService patientService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.doctorService = doctorService;
        this.passwordEncoder = passwordEncoder;
        this.nurseService = nurseService;
        this.patientService = patientService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("========== 开始初始化数据 ==========");
        initializeDepartments();  // 先初始化科室，因为医生创建需要科室
        initializeUsers();        // 再初始化用户（会自动创建角色表）
        System.out.println("========== 数据初始化完成 ==========");
    }
    
    /**
     * 初始化用户数据（会自动创建对应的角色表记录）
     */
    private void initializeUsers() {
        System.out.println("【开始初始化用户数据】");
        
        if (userService.findByPhone("13800000000") == null) {
            User admin = new User();
            admin.setPassword("admin123");  // 不要预先加密，saveUser 方法会加密
            admin.setName("系统管理员");
            admin.setRole("ADMIN");
            admin.setGender("male");
            admin.setPhone("13800000000");
            admin.setEmail("admin@hospital.com");
            admin.setAge(35);  // 设置年龄
            userService.saveUser(admin);
            System.out.println("  - 已创建管理员：系统管理员");
        }
        
        // 为每个科室创建一个医生
        String[] departments = {"全科", "内科", "外科", "耳鼻喉", "妇产科"};
        String[] doctorNames = {"刘医生", "张医生", "王医生", "陈医生", "赵医生"};
        String[] specialties = {"全科医疗", "心血管疾病", "普外科", "耳鼻喉科", "妇产科"};
        String[] phones = {"13800000001", "13800000004", "13800000005", "13800000006", "13800000007"};
        int[] ages = {45, 40, 42, 38, 39};
        
        for (int i = 0; i < departments.length; i++) {
            String departmentName = departments[i];
            String doctorName = doctorNames[i];
            String specialty = specialties[i];
            String phone = phones[i];
            int age = ages[i];
            
            if (userService.findByPhone(phone) == null) {
                User doctorUser = new User();
                doctorUser.setPassword("doctor123");  // 不要预先加密，saveUser 方法会加密
                doctorUser.setName(doctorName);
                doctorUser.setRole("DOCTOR");
                doctorUser.setGender("male");
                doctorUser.setPhone(phone);
                doctorUser.setEmail("doctor" + (i+1) + "@hospital.com");
                doctorUser.setAge(age);  // 设置年龄
                userService.saveUser(doctorUser);
                
                // 补充医生角色表详细信息
                User savedDoctor = userService.findByPhone(phone);
                if (savedDoctor != null) {
                    Doctor doctor = doctorService.findByUserId(savedDoctor.getId());
                    if (doctor != null) {
                        Department dept = departmentService.findByNameContaining(departmentName).get(0);
                        doctor.setDepartment(dept);
                        doctor.setTitle("主任医师");
                        doctor.setSpecialty(specialty);
                        doctor.setSchedule("周一至周五 8:00-12:00");
                        doctorService.saveDoctor(doctor);
                        System.out.println("  - 已创建医生用户：" + doctorName + "，同步创建医生角色表记录（" + departmentName + "/主任医师）");
                    }
                }
            }
        }
        
        if (userService.findByPhone("13800000002") == null) {
            User nurse1 = new User();
            nurse1.setPassword("nurse123");  // 不要预先加密，saveUser 方法会加密
            nurse1.setName("李护士");
            nurse1.setRole("NURSE");
            nurse1.setGender("female");
            nurse1.setPhone("13800000002");
            nurse1.setEmail("nurse1@hospital.com");
            nurse1.setAge(28);  // 设置年龄
            userService.saveUser(nurse1);
            
            // 补充护士角色表详细信息
            User savedNurse = userService.findByPhone("13800000002");
            if (savedNurse != null) {
                Nurse nurse = nurseService.findByUser(savedNurse);
                if (nurse != null) {
                    nurseService.saveNurse(nurse);
                    System.out.println("  - 已创建护士用户：李护士，同步创建护士角色表记录");
                }
            }
        }
        
        if (userService.findByPhone("13800000003") == null) {
            User patient1 = new User();
            patient1.setPassword("patient123");  // 不要预先加密，saveUser 方法会加密
            patient1.setName("王患者");
            patient1.setRole("PATIENT");
            patient1.setGender("male");
            patient1.setPhone("13800000003");
            patient1.setEmail("patient1@hospital.com");
            patient1.setAge(50);  // 设置年龄
            userService.saveUser(patient1);
            
            // 补充患者角色表详细信息
            User savedPatient = userService.findByPhone("13800000003");
            if (savedPatient != null) {
                Patient patient = patientService.findByUser(savedPatient);
                if (patient != null) {
                    patient.setAllergies("青霉素过敏");
                    patient.setEmergencyContact("张小花");
                    patient.setEmergencyPhone("13900000003");
                    patientService.savePatient(patient);
                    System.out.println("  - 已创建患者用户：王患者，同步创建患者角色表记录（青霉素过敏）");
                }
            }
        }
        
        System.out.println("【用户数据初始化完成】");
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
            generalPractice.setLocation("1楼");
            departmentService.saveDepartment(generalPractice);
            System.out.println("【创建科室】全科");
        } else {
            // 更新已存在的科室位置
            Department generalPractice = departmentService.findByNameContaining("全科").get(0);
            if (generalPractice.getLocation() == null) {
                generalPractice.setLocation("1楼");
                departmentService.saveDepartment(generalPractice);
                System.out.println("【更新科室】全科 - 添加位置: 1楼");
            } else {
                System.out.println("【已存在】全科");
            }
        }
        
        // 内科
        if (departmentService.findByNameContaining("内科").isEmpty()) {
            Department internalMedicine = new Department();
            internalMedicine.setName("内科");
            internalMedicine.setDescription("内科疾病诊疗");
            internalMedicine.setLocation("2楼");
            departmentService.saveDepartment(internalMedicine);
            System.out.println("【创建科室】内科");
        } else {
            // 更新已存在的科室位置
            Department internalMedicine = departmentService.findByNameContaining("内科").get(0);
            if (internalMedicine.getLocation() == null) {
                internalMedicine.setLocation("2楼");
                departmentService.saveDepartment(internalMedicine);
                System.out.println("【更新科室】内科 - 添加位置: 2楼");
            } else {
                System.out.println("【已存在】内科");
            }
        }
        
        // 外科
        if (departmentService.findByNameContaining("外科").isEmpty()) {
            Department surgery = new Department();
            surgery.setName("外科");
            surgery.setDescription("外科手术及治疗");
            surgery.setLocation("3楼");
            departmentService.saveDepartment(surgery);
            System.out.println("【创建科室】外科");
        } else {
            // 更新已存在的科室位置
            Department surgery = departmentService.findByNameContaining("外科").get(0);
            if (surgery.getLocation() == null) {
                surgery.setLocation("3楼");
                departmentService.saveDepartment(surgery);
                System.out.println("【更新科室】外科 - 添加位置: 3楼");
            } else {
                System.out.println("【已存在】外科");
            }
        }
        
        // 耳鼻喉
        if (departmentService.findByNameContaining("耳鼻喉").isEmpty()) {
            Department ent = new Department();
            ent.setName("耳鼻喉");
            ent.setDescription("耳鼻喉科疾病诊疗");
            ent.setLocation("4楼");
            departmentService.saveDepartment(ent);
            System.out.println("【创建科室】耳鼻喉");
        } else {
            // 更新已存在的科室位置
            Department ent = departmentService.findByNameContaining("耳鼻喉").get(0);
            if (ent.getLocation() == null) {
                ent.setLocation("4楼");
                departmentService.saveDepartment(ent);
                System.out.println("【更新科室】耳鼻喉 - 添加位置: 4楼");
            } else {
                System.out.println("【已存在】耳鼻喉");
            }
        }
        
        // 妇产科
        if (departmentService.findByNameContaining("妇产科").isEmpty()) {
            Department gynecology = new Department();
            gynecology.setName("妇产科");
            gynecology.setDescription("妇科产科疾病诊疗");
            gynecology.setLocation("5楼");
            departmentService.saveDepartment(gynecology);
            System.out.println("【创建科室】妇产科");
        } else {
            // 更新已存在的科室位置
            Department gynecology = departmentService.findByNameContaining("妇产科").get(0);
            if (gynecology.getLocation() == null) {
                gynecology.setLocation("5楼");
                departmentService.saveDepartment(gynecology);
                System.out.println("【更新科室】妇产科 - 添加位置: 5楼");
            } else {
                System.out.println("【已存在】妇产科");
            }
        }
        
        System.out.println("========================================");
        System.out.println("【科室初始化完成】共5个科室：全科、内科、外科、耳鼻喉、妇产科");
        System.out.println("========================================");
    }
    
    /**
     * 初始化医生数据（备用方法，现已由 saveUser 自动完成）
     * 此方法保留用于手动修复医生角色表数据的情况
     */
    @SuppressWarnings("unused")
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
     * 初始化护士数据（备用方法，现已由 saveUser 自动完成）
     * 此方法保留用于手动修复护士角色表数据的情况
     */
    @SuppressWarnings("unused")
    private void initializeNurses() {
        System.out.println("========================================");
        System.out.println("【初始化护士数据】开始检查...");
        
        List<User> nurseUsers = userService.findByRole("NURSE");
        System.out.println("【护士用户数量】" + nurseUsers.size());
        
        // 默认科室：全科
        Department defaultDept = departmentService.findByNameContaining("全科").get(0);
        
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
                nurse.setDepartment(defaultDept);
                nurse.setWard("普通病房");
                nurse.setTitle("护士");
                nurseService.saveNurse(nurse);
                
                System.out.println("【创建成功】护士 ID: " + nurse.getId());
            }
        }
        
        System.out.println("========================================");
        System.out.println("【护士初始化完成】");
        System.out.println("========================================");
    }
    
    /**
     * 初始化患者数据
     */
    private void initializePatients() {
        System.out.println("========================================");
        System.out.println("【初始化患者数据】开始检查...");
        
        List<User> patientUsers = userService.findByRole("PATIENT");
        System.out.println("【患者用户数量】" + patientUsers.size());
        
        for (User user : patientUsers) {
            Patient existingPatient = patientService.findByUserId(user.getId());
            if (existingPatient == null) {
                System.out.println("【创建患者记录】用户：" + user.getName() + ", ID: " + user.getId());
                
                Patient patient = new Patient();
                patient.setUser(user);
                patientService.save(patient);
                
                System.out.println("【创建成功】患者 ID: " + patient.getId() + ", 用户: " + user.getName());
            } else {
                System.out.println("【已存在】用户：" + user.getName() + ", 患者 ID: " + existingPatient.getId());
            }
        }
        
        System.out.println("========================================");
        System.out.println("【患者初始化完成】");
        System.out.println("========================================");
    }
}

