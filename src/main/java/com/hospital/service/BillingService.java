package com.hospital.service;

import com.hospital.entity.Billing;
import com.hospital.entity.Patient;
import com.hospital.repository.BillingRepository;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingService {
    
    @Autowired
    private BillingRepository billingRepository;
    
    @Autowired
    private PatientService patientService;
    
    public Billing saveBilling(Billing billing) {
        billing.setCreateTime(LocalDateTime.now());
        return billingRepository.save(billing);
    }
    
    public Billing findById(Long id) {
        return billingRepository.findById(id).orElse(null);
    }
    
    public List<Billing> findByPatientId(Long patientId) {
        return billingRepository.findByPatientId(patientId);
    }
    
    public List<Billing> findByUserId(Long userId) {
        // 根据 userId 找到对应的 patientId，然后调用 findByPatientId 方法
        Patient patient = patientService.findByUserId(userId);
        if (patient != null) {
            return findByPatientId(patient.getId());
        }
        return java.util.Collections.emptyList();
    }
    
    public List<Billing> findByStatus(String status) {
        return billingRepository.findByStatus(status);
    }
    
    public List<Billing> findAll() {
        return billingRepository.findAll();
    }
    
    public Billing updateBilling(Billing billing) {
        return billingRepository.save(billing);
    }
    
    public void deleteBilling(Long id) {
        billingRepository.deleteById(id);
    }
}