package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "patient/dashboard";
    }
    
    @GetMapping("/appointments")
    public String appointments(Model model, Authentication authentication) {
        User user = userService.findByPhone(authentication.getName());
        List<Appointment> appointments = appointmentService.findByUserId(user.getId());
        model.addAttribute("appointments", appointments);
        return "patient/appointments";
    }
    
    @GetMapping("/book")
    public String bookAppointment(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "patient/book";
    }
    
    @GetMapping("/doctors")
    public String getDoctors(@RequestParam Long departmentId, Model model) {
        List<Doctor> doctors = doctorService.findByDepartmentId(departmentId);
        model.addAttribute("doctors", doctors);
        return "patient/doctors :: doctorsList";
    }
    
    @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId, @RequestParam Long departmentId,
                                  @RequestParam String appointmentTime, @RequestParam String symptoms,
                                  Authentication authentication) {
        
        User user = userService.findByPhone(authentication.getName());
        Doctor doctor = doctorService.findById(doctorId);
        Department department = departmentService.findById(departmentId);
        
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setDepartment(department);
        appointment.setAppointmentTime(LocalDateTime.parse(appointmentTime));
        appointment.setSymptoms(symptoms);
        
        appointmentService.saveAppointment(appointment);
        
        return "redirect:/patient/appointments";
    }
    
    @GetMapping("/departments")
    public String departments(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "patient/departments";
    }
    
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.findByPhone(authentication.getName());
        model.addAttribute("user", user);
        return "patient/profile";
    }
    
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name, @RequestParam String gender,
                               @RequestParam String phone, @RequestParam String email,
                               Authentication authentication) {
        User user = userService.findByPhone(authentication.getName());
        user.setName(name);
        user.setGender(gender);
        user.setPhone(phone);
        user.setEmail(email);
        userService.updateUser(user);
        
        return "redirect:/patient/profile";
    }
}