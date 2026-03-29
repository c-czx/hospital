package com.hospital.service;

import com.hospital.entity.Prescription;
import com.hospital.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    public Prescription savePrescription(Prescription prescription) {
        prescription.setCreateTime(LocalDateTime.now());
        return prescriptionRepository.save(prescription);
    }
    
    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id).orElse(null);
    }
    
    public List<Prescription> findByUserId(Long userId) {
        return prescriptionRepository.findByUserId(userId);
    }
    
    public List<Prescription> findByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }
    
    public List<Prescription> findAll() {
        return prescriptionRepository.findAll();
    }
    
    public Prescription updatePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }
    
    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }
}