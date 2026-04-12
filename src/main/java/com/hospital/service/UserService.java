package com.hospital.service;

import com.hospital.entity.User;
import com.hospital.entity.Department;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.entity.Nurse;
import com.hospital.repository.UserRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private NurseRepository nurseRepository;
    
    /**
     * 保存用户并自动创建对应的角色表记录
     */
    @Transactional
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        // 根据角色自动创建对应的角色表记录（管理员除外）
        String role = user.getRole();
        if ("DOCTOR".equals(role)) {
            createDoctorRecord(savedUser);
            System.out.println("【已为医生 " + savedUser.getName() + " 创建角色表记录】");
        } else if ("NURSE".equals(role)) {
            createNurseRecord(savedUser);
            System.out.println("【已为护士 " + savedUser.getName() + " 创建角色表记录】");
        } else if ("PATIENT".equals(role)) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setAllergies("无");
            patientRepository.save(patient);
            System.out.println("【已为患者 " + savedUser.getName() + " 创建角色表记录】");
        }
        // ADMIN 不需要创建角色表记录
        
        return savedUser;
    }
    
    @Transactional
    public void createDoctorRecord(User user) {
        List<Doctor> existingDoctors = doctorRepository.findByUserId(user.getId());
        if (existingDoctors.isEmpty()) {
            try {
                List<Department> depts = departmentRepository.findByNameContaining("内科");
                if (!depts.isEmpty()) {
                    Department defaultDept = depts.get(0);
                    
                    Doctor doctor = new Doctor();
                    doctor.setUser(user);
                    doctor.setDepartment(defaultDept);
                    doctor.setTitle("住院医师");
                    doctor.setSpecialty("普通内科");
                    doctor.setSchedule("周一至周五 8:00-12:00");
                    doctorRepository.save(doctor);
                    System.out.println("【创建医生记录成功】用户 ID: " + user.getId() + "，医生 ID: " + doctor.getId());
                }
            } catch (Exception e) {
                System.err.println("【创建医生记录失败】" + e.getMessage());
            }
        }
    }
    
    @Transactional
    public void createNurseRecord(User user) {
        nurseRepository.findByUserId(user.getId()).ifPresent(existingNurse -> {
            throw new RuntimeException("护士记录已存在");
        });
        
        try {
            Nurse nurse = new Nurse();
            nurse.setUser(user);
            // 设置默认科室（如果存在）
            List<Department> depts = departmentRepository.findAll();
            if (!depts.isEmpty()) {
                nurse.setDepartment(depts.get(0));
            }
            // 设置默认病区和职称
            nurse.setWard("普通病区");
            nurse.setTitle("护士");
            nurseRepository.save(nurse);
            System.out.println("【创建护士记录成功】用户 ID: " + user.getId() + "，护士 ID: " + nurse.getId());
        } catch (Exception e) {
            System.err.println("【创建护士记录失败】" + e.getMessage());
        }
    }
    
    @Transactional
    public void createPatientRecord(User user) {
        Patient existingPatient = patientRepository.findByUser(user);
        if (existingPatient == null) {
            try {
                Patient patient = new Patient(user);
                patient.setUser(user);
                patient.setAllergies("无");
                patientRepository.save(patient);
                System.out.println("【创建患者记录成功】用户 ID: " + user.getId() + "，患者 ID: " + patient.getId());
            } catch (Exception e) {
                System.err.println("【创建患者记录失败】" + e.getMessage());
            }
        }
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }
    
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * 更新用户信息，同时同步角色表
     */
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            return null;
        }
        
        String oldRole = existingUser.getRole();
        String newRole = user.getRole();
        
        // 如果角色发生变化，需要同步角色表
        if (!oldRole.equals(newRole)) {
            System.out.println("【用户 " + existingUser.getName() + " 角色变更：" + oldRole + " -> " + newRole + "】");
            
            // 1. 从旧角色表中删除
            removeFromOldRoleTable(existingUser.getId(), oldRole);
            
            // 2. 在新角色表中创建（如果需要）
            addToNewRoleTable(existingUser, newRole);
        }
        
        // 更新用户信息
        existingUser.setName(user.getName());
        existingUser.setRole(newRole);
        existingUser.setGender(user.getGender());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        
        // 如果密码不为空，更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * 从旧角色表中删除用户
     */
    private void removeFromOldRoleTable(Long userId, String oldRole) {
        if ("DOCTOR".equals(oldRole)) {
            List<Doctor> doctors = doctorRepository.findByUserId(userId);
            if (!doctors.isEmpty()) {
                doctorRepository.deleteAll(doctors);
                System.out.println("  - 已从医生表中删除");
            }
        } else if ("NURSE".equals(oldRole)) {
            nurseRepository.findByUserId(userId).ifPresent(nurse -> {
                nurseRepository.delete(nurse);
                System.out.println("  - 已从护士表中删除");
            });
        } else if ("PATIENT".equals(oldRole)) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                Patient patient = patientRepository.findByUser(user);
                if (patient != null) {
                    patientRepository.delete(patient);
                    System.out.println("  - 已从患者表中删除");
                }
            }
        }
    }
    
    /**
     * 在新角色表中创建用户
     */
    private void addToNewRoleTable(User user, String newRole) {
        if ("DOCTOR".equals(newRole)) {
            createDoctorRecord(user);
        } else if ("NURSE".equals(newRole)) {
            createNurseRecord(user);
            System.out.println("  - 已在护士表中创建");
        } else if ("PATIENT".equals(newRole)) {
            createPatientRecord(user);
            System.out.println("  - 已在患者表中创建");
        }
        // ADMIN 不需要创建角色表记录
    }
    
    /**
     * 删除用户，同时清理相关角色表中的数据
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return;
        }
        
        // 1. 检查并删除医生表中的记录
        List<Doctor> doctors = doctorRepository.findByUserId(id);
        if (!doctors.isEmpty()) {
            doctorRepository.deleteAll(doctors);
            System.out.println("【已从医生表中删除用户：" + user.getName() + "】");
        }
        
        // 2. 检查并删除护士表中的记录
        nurseRepository.findByUserId(id).ifPresent(nurse -> {
            nurseRepository.delete(nurse);
            System.out.println("【已从护士表中删除用户：" + user.getName() + "】");
        });
        
        // 3. 检查并删除患者表中的记录
        Patient patient = patientRepository.findByUser(user);
        if (patient != null) {
            patientRepository.delete(patient);
            System.out.println("【已从患者表中删除用户：" + user.getName() + "】");
        }
        
        // 4. 最后删除用户
        userRepository.deleteById(id);
        System.out.println("【已删除用户：" + user.getName() + "】");
    }
    
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    
    /**
     * 为所有已存在的患者用户创建对应的患者记录
     */
    @Transactional
    public void createPatientRecordsForExistingUsers() {
        List<User> patientUsers = userRepository.findByRole("PATIENT");
        int createdCount = 0;
        
        for (User user : patientUsers) {
            Patient existingPatient = patientRepository.findByUser(user);
            if (existingPatient == null) {
                try {
                    Patient patient = new Patient(user);
                    patientRepository.save(patient);
                    createdCount++;
                    System.out.println("【为现有用户创建患者记录】用户ID: " + user.getId() + "，用户名: " + user.getName() + "，患者ID: " + patient.getId());
                } catch (Exception e) {
                    System.err.println("【创建患者记录失败】用户ID: " + user.getId() + "，错误: " + e.getMessage());
                }
            }
        }
        
        System.out.println("【患者记录创建完成】共处理 " + patientUsers.size() + " 个患者用户，新创建 " + createdCount + " 个患者记录");
    }
}
