package com.hospital.service;

import com.hospital.entity.Billing;
import com.hospital.repository.BillingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingService {
    
    @Autowired
    private BillingRepository billingRepository;
    
    public Billing saveBilling(Billing billing) {
        billing.setCreateTime(LocalDateTime.now());
        return billingRepository.save(billing);
    }
    
    public Billing findById(Long id) {
        return billingRepository.findById(id).orElse(null);
    }
    
    public List<Billing> findByUserId(Long userId) {
        return billingRepository.findByUserId(userId);
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