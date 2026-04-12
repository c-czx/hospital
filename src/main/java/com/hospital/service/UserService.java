package com.hospital.service;

import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.entity.Department;
import com.hospital.entity.Patient;
import com.hospital.repository.UserRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

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
    
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        if ("DOCTOR".equals(user.getRole())) {
            createDoctorRecord(savedUser);
        } else if ("PATIENT".equals(user.getRole())) {
            createPatientRecord(savedUser);
        }
        
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
                }
            } catch (Exception e) {
                System.err.println("【创建医生记录失败】" + e.getMessage());
            }
        }
    }
    
    @Transactional
    public void createPatientRecord(User user) {
        Patient existingPatient = patientRepository.findByUser(user);
        if (existingPatient == null) {
            try {
                Patient patient = new Patient(user);
                patientRepository.save(patient);
                System.out.println("【创建患者记录成功】用户ID: " + user.getId() + "，患者ID: " + patient.getId());
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
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
