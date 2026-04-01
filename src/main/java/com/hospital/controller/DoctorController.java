package com.hospital.controller;

import com.hospital.entity.Advice;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Prescription;
import com.hospital.entity.Schedule;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.service.DoctorService;
import com.hospital.service.UserService;
import com.hospital.service.AdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AdviceService adviceService;
    
    private Long getCurrentDoctorId(Authentication authentication) {
        String phone = authentication.getName();
        User user = userService.findByPhone(phone);
        if (user == null) {
            return null;
        }
        Doctor doctor = doctorService.findByUserId(user.getId());
        return doctor != null ? doctor.getId() : null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("todayList", doctorService.getTodayPatient(doctorId).get("data"));
        return "doctor/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("allAppointments", doctorService.getAllAppointments(doctorId).get("data"));
        return "doctor/appointments";
    }

    @GetMapping("/appointment-detail")
    public String appointmentDetail(@RequestParam Long patientId, Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("patient", doctorService.getPatientDetail(patientId).get("data"));
        model.addAttribute("adviceList", doctorService.getAdviceList(patientId).get("data"));
        return "doctor/appointment-detail";
    }

    @GetMapping("/prescription-form")
    public String prescriptionForm(@RequestParam Long patientId, Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("patientId", patientId);
        model.addAttribute("doctorId", doctorId);
        return "doctor/prescription-form";
    }

    @GetMapping("/schedule")
    public String schedule(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("scheduleList", doctorService.getScheduleList(doctorId).get("data"));
        return "doctor/schedule";
    }
    
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        Doctor doctor = doctorService.findById(doctorId);
        model.addAttribute("doctor", doctor);
        return "doctor/profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name, @RequestParam String gender,
                               @RequestParam Integer age, @RequestParam String phone,
                               @RequestParam String email, @RequestParam String title,
                               @RequestParam String specialty, @RequestParam String schedule,
                               Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        
        Doctor doctor = doctorService.findById(doctorId);
        User user = doctor.getUser();
        
        user.setName(name);
        user.setGender(gender);
        user.setAge(age);
        user.setPhone(phone);
        user.setEmail(email);
        userService.updateUser(user);
        
        doctor.setTitle(title);
        doctor.setSpecialty(specialty);
        doctor.setSchedule(schedule);
        doctorService.updateDoctor(doctor);
        
        return "redirect:/doctor/profile";
    }
    
    @GetMapping("/advices")
    public String advices(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("advices", adviceService.getAdvicesWithPatientName(doctorId));
        return "doctor/advices";
    }

    @PostMapping("/publishSchedule")
    public String publishSchedule(Schedule schedule, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        schedule.setDoctor(doctor);
        schedule.setRemainNumber(schedule.getTotalNumber());
        doctorService.publishSchedule(schedule);
        return "redirect:/doctor/schedule";
    }

    @PostMapping("/saveRecord")
    public String saveRecord(MedicalRecord record, @RequestParam Long doctorId, Authentication authentication) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        record.setDoctor(doctor);
        doctorService.saveRecord(record);
        return "redirect:/doctor/dashboard";
    }

    @PostMapping("/createPrescription")
    public String createPrescription(Prescription prescription, @RequestParam Long doctorId, Authentication authentication) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        prescription.setDoctor(doctor);
        doctorService.createPrescription(prescription);
        return "redirect:/doctor/dashboard";
    }
    
    @PostMapping("/createAdvice")
    public String createAdvice(Advice advice, @RequestParam Long doctorId, Authentication authentication) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        advice.setDoctor(doctor);
        doctorService.createAdvice(advice);
        return "redirect:/doctor/appointment-detail?patientId=" + advice.getUser().getId();
    }
    
    @PostMapping("/updateAdvice")
    public String updateAdvice(Advice advice, Authentication authentication) {
        doctorService.updateAdvice(advice);
        return "redirect:/doctor/appointment-detail?patientId=" + advice.getUser().getId();
    }
}