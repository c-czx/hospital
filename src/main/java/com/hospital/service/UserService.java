package com.hospital.service;

import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.entity.Department;
import com.hospital.repository.UserRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.DepartmentRepository;
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
    
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        if ("DOCTOR".equals(user.getRole())) {
            createDoctorRecord(savedUser);
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
}
