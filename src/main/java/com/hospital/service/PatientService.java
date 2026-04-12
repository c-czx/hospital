package com.hospital.service;

import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 患者服务类
 * 提供患者相关的业务逻辑操作
 */
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * 保存患者信息
     */
    @Transactional
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * 根据 ID 查找患者
     */
    public Patient findById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    /**
     * 根据用户查找患者
     */
    public Patient findByUser(User user) {
        return patientRepository.findByUser(user);
    }

    /**
     * 查找所有患者
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * 更新患者信息
     */
    @Transactional
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * 删除患者
     */
    @Transactional
    public void deletePatient(Patient patient) {
        patientRepository.delete(patient);
    }

    /**
     * 根据 ID 删除患者
     */
    @Transactional
    public void deletePatientById(Long id) {
        patientRepository.deleteById(id);
    }
}
