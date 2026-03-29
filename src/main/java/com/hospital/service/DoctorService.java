package com.hospital.service;

import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    public Doctor findById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }
    
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }
    
    public List<Doctor> findByDepartmentId(Long departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }
    
    public Doctor findByUserId(Long userId) {
        List<Doctor> doctors = doctorRepository.findByUserId(userId);
        return doctors.isEmpty() ? null : doctors.get(0);
    }
    
    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
}