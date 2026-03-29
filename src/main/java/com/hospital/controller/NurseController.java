package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/nurse")
public class NurseController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private BillingService billingService;
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "nurse/dashboard";
    }
    
    @GetMapping("/patients")
    public String patients(Model model) {
        List<User> patients = userService.findByRole("patient");
        model.addAttribute("patients", patients);
        return "nurse/patients";
    }
    
    @GetMapping("/patient/{id}")
    public String patientDetail(@PathVariable Long id, Model model) {
        User patient = userService.findById(id);
        List<Appointment> appointments = appointmentService.findByUserId(id);
        List<MedicalRecord> medicalRecords = medicalRecordService.findByUserId(id);
        List<Billing> billings = billingService.findByUserId(id);
        
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("medicalRecords", medicalRecords);
        model.addAttribute("billings", billings);
        
        return "nurse/patient-detail";
    }
    
    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        return "nurse/appointments";
    }
    
    @GetMapping("/billing/create")
    public String createBilling(@RequestParam Long userId, Model model) {
        User patient = userService.findById(userId);
        model.addAttribute("patient", patient);
        return "nurse/billing-form";
    }
    
    @PostMapping("/billing/create")
    public String saveBilling(@RequestParam Long userId, @RequestParam String type,
                             @RequestParam BigDecimal amount, @RequestParam String status) {
        
        User patient = userService.findById(userId);
        
        Billing billing = new Billing();
        billing.setUser(patient);
        billing.setType(type);
        billing.setAmount(amount);
        billing.setStatus(status);
        billingService.saveBilling(billing);
        
        return "redirect:/nurse/patient/" + userId;
    }
    
    @GetMapping("/billing/{id}/pay")
    public String payBilling(@PathVariable Long id) {
        Billing billing = billingService.findById(id);
        billing.setStatus("已支付");
        billingService.updateBilling(billing);
        
        return "redirect:/nurse/patient/" + billing.getUser().getId();
    }
}