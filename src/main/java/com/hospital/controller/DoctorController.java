package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByPhone(authentication.getName());
        Doctor doctor = doctorService.findByUserId(user.getId());
        
        if (doctor != null) {
            List<Appointment> appointments = appointmentService.findByDoctorId(doctor.getId());
            model.addAttribute("appointments", appointments);
            model.addAttribute("doctor", doctor);
        }
        
        return "doctor/dashboard";
    }
    
    @GetMapping("/appointments")
    public String appointments(Authentication authentication, Model model) {
        User user = userService.findByPhone(authentication.getName());
        Doctor doctor = doctorService.findByUserId(user.getId());
        
        if (doctor != null) {
            List<Appointment> appointments = appointmentService.findByDoctorId(doctor.getId());
            model.addAttribute("appointments", appointments);
        }
        
        return "doctor/appointments";
    }
    
    @GetMapping("/appointment/{id}")
    public String appointmentDetail(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.findById(id);
        model.addAttribute("appointment", appointment);
        return "doctor/appointment-detail";
    }
    
    @PostMapping("/appointment/{id}/complete")
    public String completeAppointment(@PathVariable Long id, @RequestParam String diagnosis,
                                     @RequestParam String treatment, @RequestParam String notes) {
        Appointment appointment = appointmentService.findById(id);
        
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setUser(appointment.getUser());
        medicalRecord.setDoctor(appointment.getDoctor());
        medicalRecord.setDiagnosis(diagnosis);
        medicalRecord.setTreatment(treatment);
        medicalRecord.setNotes(notes);
        medicalRecordService.saveMedicalRecord(medicalRecord);
        
        appointment.setStatus("已完成");
        appointmentService.updateAppointment(appointment);
        
        return "redirect:/doctor/appointments";
    }
    
    @GetMapping("/prescription/create")
    public String createPrescription(@RequestParam Long userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);
        return "doctor/prescription-form";
    }
    
    @PostMapping("/prescription/create")
    public String savePrescription(@RequestParam Long userId, @RequestParam Long doctorId,
                                   @RequestParam String medicines, @RequestParam String dosage,
                                   @RequestParam String instructions) {
        
        User user = userService.findById(userId);
        Doctor doctor = doctorService.findById(doctorId);
        
        Prescription prescription = new Prescription();
        prescription.setUser(user);
        prescription.setDoctor(doctor);
        prescription.setMedicines(medicines);
        prescription.setDosage(dosage);
        prescription.setInstructions(instructions);
        prescriptionService.savePrescription(prescription);
        
        return "redirect:/doctor/appointments";
    }
    
    @GetMapping("/schedule")
    public String schedule(Authentication authentication, Model model) {
        User user = userService.findByPhone(authentication.getName());
        Doctor doctor = doctorService.findByUserId(user.getId());
        model.addAttribute("doctor", doctor);
        return "doctor/schedule";
    }
    
    @PostMapping("/schedule")
    public String updateSchedule(@RequestParam Long doctorId, @RequestParam String schedule) {
        Doctor doctor = doctorService.findById(doctorId);
        doctor.setSchedule(schedule);
        doctorService.updateDoctor(doctor);
        
        return "redirect:/doctor/schedule";
    }
}