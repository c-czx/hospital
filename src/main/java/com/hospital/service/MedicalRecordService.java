package com.hospital.service;

import com.hospital.entity.MedicalRecord;
import com.hospital.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecord.setCreateTime(LocalDateTime.now());
        return medicalRecordRepository.save(medicalRecord);
    }
    
    public MedicalRecord findById(Long id) {
        return medicalRecordRepository.findById(id).orElse(null);
    }
    
    public List<MedicalRecord> findByUserId(Long userId) {
        return medicalRecordRepository.findByUserId(userId);
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