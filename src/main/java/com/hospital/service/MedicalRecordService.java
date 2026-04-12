package com.hospital.service;

import com.hospital.entity.MedicalRecord;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecord.setCreateTime(LocalDateTime.now());
        return medicalRecordRepository.save(medicalRecord);
    }
    
    public MedicalRecord findById(Long id) {
        return medicalRecordRepository.findById(id).orElse(null);
    }
    
    public List<MedicalRecord> findByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }
    
    public List<MedicalRecord> findByUserId(Long userId) {
        // 先根据用户ID找到对应的患者ID
        com.hospital.entity.Patient patient = patientRepository.findByUserId(userId);
        if (patient != null) {
            return medicalRecordRepository.findByPatientId(patient.getId());
        }
        return null;
    }
    
    public List<MedicalRecord> findByDoctorId(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId);
    }
    
    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }
    
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }
    
    public void deleteMedicalRecord(Long id) {
        medicalRecordRepository.deleteById(id);
    }
}