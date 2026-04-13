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
        List<Billing> billings = billingRepository.findByPatientId(patientId);
        // 去重：根据缴费类型和appointment_id进行去重，保留已支付状态的记录，如果没有已支付状态的则保留待支付状态的
        return billings.stream()
                .collect(java.util.stream.Collectors.toMap(
                        billing -> billing.getType() + (billing.getAppointment() != null ? "_" + billing.getAppointment().getId() : "_null"),
                        billing -> billing,
                        (existing, replacement) -> {
                            // 如果现有记录是已支付状态，保留现有记录
                            if ("已支付".equals(existing.getStatus())) {
                                return existing;
                            }
                            // 否则保留新记录
                            return replacement;
                        }
                ))
                .values()
                .stream()
                .collect(java.util.stream.Collectors.toList());
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