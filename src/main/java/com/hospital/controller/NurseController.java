package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private CheckupService checkupService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "nurse/dashboard";
    }
    
    @GetMapping("/patients")
    public String patients(Model model) {
        List<User> patients = userService.findByRole("PATIENT");
        model.addAttribute("patients", patients);
        return "nurse/patients";
    }
    
    @GetMapping("/billing/patients")
    public String billingPatients(Model model) {
        // 获取所有患者
        List<User> patients = userService.findByRole("PATIENT");
        
        // 计算每个患者的总费用、已支付费用和待支付费用
        java.util.Map<Long, java.math.BigDecimal> totalAmounts = new java.util.HashMap<>();
        java.util.Map<Long, java.math.BigDecimal> paidAmounts = new java.util.HashMap<>();
        java.util.Map<Long, java.math.BigDecimal> unpaidAmounts = new java.util.HashMap<>();
        
        for (User patient : patients) {
            // 查找对应的 Patient 对象
            com.hospital.entity.Patient patientEntity = patientService.findByUserId(patient.getId());
            if (patientEntity != null) {
                // 获取患者的所有缴费记录
                List<Billing> billings = billingService.findByPatientId(patientEntity.getId());
                
                // 计算总费用、已支付费用和待支付费用
                java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                java.math.BigDecimal paid = java.math.BigDecimal.ZERO;
                java.math.BigDecimal unpaid = java.math.BigDecimal.ZERO;
                
                for (Billing billing : billings) {
                    total = total.add(billing.getAmount());
                    if ("已支付".equals(billing.getStatus())) {
                        paid = paid.add(billing.getAmount());
                    } else {
                        unpaid = unpaid.add(billing.getAmount());
                    }
                }
                
                totalAmounts.put(patient.getId(), total);
                paidAmounts.put(patient.getId(), paid);
                unpaidAmounts.put(patient.getId(), unpaid);
            } else {
                totalAmounts.put(patient.getId(), java.math.BigDecimal.ZERO);
                paidAmounts.put(patient.getId(), java.math.BigDecimal.ZERO);
                unpaidAmounts.put(patient.getId(), java.math.BigDecimal.ZERO);
            }
        }
        
        model.addAttribute("patients", patients);
        model.addAttribute("totalAmounts", totalAmounts);
        model.addAttribute("paidAmounts", paidAmounts);
        model.addAttribute("unpaidAmounts", unpaidAmounts);
        return "nurse/billing-patients";
    }
    
    @GetMapping("/patient/{id}")
    public String patientDetail(@PathVariable Long id, Model model) {
        User patient = userService.findById(id);
        
        // 查找对应的 Patient 对象
        com.hospital.entity.Patient patientEntity = patientService.findByUserId(id);
        if (patientEntity == null) {
            // 如果找不到对应的 Patient 对象，创建一个新的
            patientEntity = new com.hospital.entity.Patient();
            patientEntity.setUser(patient);
            patientEntity = patientService.save(patientEntity);
        }
        
        List<Appointment> appointments = appointmentService.findByPatientId(patientEntity.getId());
        List<MedicalRecord> medicalRecords = medicalRecordService.findByPatientId(patientEntity.getId());
        List<Billing> billings = billingService.findByPatientId(patientEntity.getId());
        
        // 计算总费用、已支付费用和待支付费用
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        java.math.BigDecimal paid = java.math.BigDecimal.ZERO;
        java.math.BigDecimal unpaid = java.math.BigDecimal.ZERO;
        
        for (Billing billing : billings) {
            total = total.add(billing.getAmount());
            if ("已支付".equals(billing.getStatus())) {
                paid = paid.add(billing.getAmount());
            } else {
                unpaid = unpaid.add(billing.getAmount());
            }
        }
        
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("medicalRecords", medicalRecords);
        model.addAttribute("billings", billings);
        model.addAttribute("totalAmount", total);
        model.addAttribute("paidAmount", paid);
        model.addAttribute("unpaidAmount", unpaid);
        
        return "nurse/patient-detail";
    }
    
    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        return "nurse/appointments";
    }
    
    @GetMapping("/checkups")
    public String checkups(Model model) {
        // 获取所有检查记录
        List<Checkup> checkups = checkupService.findAll();
        model.addAttribute("checkups", checkups);
        return "nurse/checkups";
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
        
        // 先根据 User 对象找到对应的 Patient 对象
        com.hospital.entity.Patient patientEntity = patientService.findByUser(patient);
        
        Billing billing = new Billing();
        billing.setPatient(patientEntity);
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
        
        return "redirect:/nurse/patient/" + billing.getPatient().getUser().getId();
    }
    
    @GetMapping("/billing/patient/{id}/pay")
    public String payPatientBilling(@PathVariable Long id) {
        // 查找对应的 Patient 对象
        com.hospital.entity.Patient patientEntity = patientService.findByUserId(id);
        if (patientEntity != null) {
            // 获取患者的所有缴费记录
            List<Billing> billings = billingService.findByPatientId(patientEntity.getId());
            // 将所有待支付的缴费记录更新为已支付
            for (Billing billing : billings) {
                if ("待支付".equals(billing.getStatus())) {
                    billing.setStatus("已支付");
                    billingService.updateBilling(billing);
                }
            }
        }
        
        return "redirect:/nurse/billing/patients";
    }
    
    @GetMapping("/medical-record/edit/{id}")
    public String editMedicalRecord(@PathVariable Long id, Model model) {
        MedicalRecord record = medicalRecordService.findById(id);
        model.addAttribute("record", record);
        model.addAttribute("patient", record.getPatient().getUser());
        return "nurse/medical-record-edit";
    }
    
    @PostMapping("/medical-record/edit/{id}")
    public String saveMedicalRecord(@PathVariable Long id, @RequestParam(required = false) Double temperature,
                                  @RequestParam(required = false) Integer bloodPressure,
                                  @RequestParam(required = false) String nurseNotes) {
        MedicalRecord record = medicalRecordService.findById(id);
        record.setTemperature(temperature);
        record.setBloodPressure(bloodPressure);
        record.setNurseNotes(nurseNotes);
        medicalRecordService.updateMedicalRecord(record);
        
        return "redirect:/nurse/patient/" + record.getPatient().getUser().getId();
    }
    
    @GetMapping("/medical-record/create")
    public String createMedicalRecord(@RequestParam Long userId, Model model) {
        User patient = userService.findById(userId);
        List<Doctor> doctors = doctorService.findAll();
        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctors);
        return "nurse/medical-record-create";
    }
    
    @PostMapping("/medical-record/create")
    public String saveNewMedicalRecord(@RequestParam Long userId, @RequestParam Long doctorId,
                                     @RequestParam(required = false) String chiefComplaint,
                                     @RequestParam(required = false) String presentIllness,
                                     @RequestParam(required = false) String diagnosisResult,
                                     @RequestParam(required = false) Double temperature,
                                     @RequestParam(required = false) Integer bloodPressure,
                                     @RequestParam(required = false) String nurseNotes) {
        User patient = userService.findById(userId);
        Doctor doctor = doctorService.findById(doctorId);
        
        // 先根据 User 对象找到对应的 Patient 对象
        com.hospital.entity.Patient patientEntity = patientService.findByUser(patient);
        
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patientEntity);
        record.setDoctor(doctor);
        record.setChiefComplaint(chiefComplaint);
        record.setPresentIllness(presentIllness);
        record.setDiagnosisResult(diagnosisResult);
        record.setTemperature(temperature);
        record.setBloodPressure(bloodPressure);
        record.setNurseNotes(nurseNotes);
        
        medicalRecordService.saveMedicalRecord(record);
        
        return "redirect:/nurse/patient/" + userId;
    }
    
    @GetMapping("/checkup/{id}")
    public String checkupDetail(@PathVariable Long id, Model model) {
        Checkup checkup = checkupService.findById(id);
        model.addAttribute("checkup", checkup);
        model.addAttribute("patient", checkup.getPatient().getUser());
        return "nurse/checkup-detail";
    }
    
    @PostMapping("/checkup/{id}/update")
    public String updateCheckup(@PathVariable Long id, @RequestParam String result, @RequestParam String description) {
        Checkup checkup = checkupService.findById(id);
        // 保存原始状态
        Integer originalStatus = checkup.getStatus();
        
        checkup.setResult(result);
        checkup.setDescription(description);
        checkup.setStatus(1); // 标记为已完成
        checkupService.save(checkup);
        
        // 只有当检查记录的状态从 0（待检查）变为 1（已完成）时，才创建缴费记录
        if (originalStatus != null && originalStatus == 0) {
            // 创建缴费记录
            createBillingForCheckup(checkup);
        }
        
        return "redirect:/nurse/checkups";
    }
    
    private void createBillingForCheckup(Checkup checkup) {
        // 固定费用：挂号费 50 元，门诊费 100 元
        BigDecimal registrationFee = new BigDecimal(50);
        BigDecimal outpatientFee = new BigDecimal(100);
        
        // 根据检查类型设置费用
        BigDecimal checkupFee = new BigDecimal(0);
        String checkupType = checkup.getType();
        if ("心电图".equals(checkupType)) {
            checkupFee = new BigDecimal(150);
        } else if ("血常规".equals(checkupType)) {
            checkupFee = new BigDecimal(80);
        } else if ("胸片".equals(checkupType)) {
            checkupFee = new BigDecimal(200);
        } else if ("B超".equals(checkupType)) {
            checkupFee = new BigDecimal(120);
        } else {
            checkupFee = new BigDecimal(100); // 默认检查费用
        }
        
        // 创建挂号费缴费记录
        Billing registrationBilling = new Billing();
        registrationBilling.setPatient(checkup.getPatient());
        registrationBilling.setType("挂号费");
        registrationBilling.setAmount(registrationFee);
        registrationBilling.setStatus("待支付");
        billingService.saveBilling(registrationBilling);
        
        // 创建门诊费缴费记录
        Billing outpatientBilling = new Billing();
        outpatientBilling.setPatient(checkup.getPatient());
        outpatientBilling.setType("门诊费");
        outpatientBilling.setAmount(outpatientFee);
        outpatientBilling.setStatus("待支付");
        billingService.saveBilling(outpatientBilling);
        
        // 创建检查费缴费记录
        Billing checkupBilling = new Billing();
        checkupBilling.setPatient(checkup.getPatient());
        checkupBilling.setType(checkupType + "检查费");
        checkupBilling.setAmount(checkupFee);
        checkupBilling.setStatus("待支付");
        billingService.saveBilling(checkupBilling);
        
        // 创建药品费用缴费记录
        createBillingForPrescription(checkup.getPatient());
    }
    
    private void createBillingForPrescription(Patient patient) {
        // 查询患者的处方信息
        List<Prescription> prescriptions = prescriptionService.findByPatientId(patient.getId());
        if (!prescriptions.isEmpty()) {
            // 获取最新的处方
            Prescription latestPrescription = prescriptions.stream()
                    .sorted((p1, p2) -> p2.getCreateTime().compareTo(p1.getCreateTime()))
                    .findFirst()
                    .orElse(null);
            
            if (latestPrescription != null && latestPrescription.getDrugList() != null && !latestPrescription.getDrugList().isEmpty()) {
                // 解析处方中的药品信息
                String drugList = latestPrescription.getDrugList();
                // 简单处理：假设药品列表格式为 "药品1,药品2,药品3"
                String[] drugs = drugList.split(",");
                
                // 为每种药品创建缴费记录
                for (String drug : drugs) {
                    drug = drug.trim();
                    if (!drug.isEmpty()) {
                        // 这里可以根据药品名称查询药品价格，这里简单设置一个默认价格
                        BigDecimal drugFee = new BigDecimal(50); // 默认药品价格
                        
                        Billing drugBilling = new Billing();
                        drugBilling.setPatient(patient);
                        drugBilling.setType(drug + "药品费");
                        drugBilling.setAmount(drugFee);
                        drugBilling.setStatus("待支付");
                        billingService.saveBilling(drugBilling);
                    }
                }
            }
        }
    }
}