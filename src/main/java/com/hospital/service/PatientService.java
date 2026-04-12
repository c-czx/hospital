package com.hospital.service;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    // 根据ID查找患者
    public Patient findById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }
    
    // 根据用户对象查找患者
    public Patient findByUser(com.hospital.entity.User user) {
        return patientRepository.findByUser(user);
    }
    
    // 根据用户ID查找患者
    public Patient findByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }
    
    // 获取所有患者
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
    
    // 保存患者信息
    @Transactional
    public void savePatient(Patient patient) {
        patientRepository.save(patient);
    }
    
    // 保存患者信息并返回保存后的对象
    @Transactional
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }
    
    // 更新患者信息
    @Transactional
    public void updatePatient(Patient patient) {
        patientRepository.save(patient);
    }
    
    // 删除患者
    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}