package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        List<Appointment> appointments = appointmentService.findByAppointmentTimeBetween(todayStart, todayEnd);
        List<User> patients = appointments.stream()
                .map(appointment -> appointment.getPatient().getUser())
                .distinct()
                .toList();
        model.addAttribute("patients", patients);
        return "nurse/patients";
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
}