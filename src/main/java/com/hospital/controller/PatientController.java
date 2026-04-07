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
import java.util.Map;

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
    @ResponseBody
    public List<Doctor> getDoctors(@RequestParam Long departmentId) {
        return doctorService.findByDepartmentId(departmentId);
    }
    
    @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId, @RequestParam Long departmentId,
                                  @RequestParam String appointmentTime, @RequestParam String symptoms,
                                  Authentication authentication) {
        
        User user = userService.findByPhone(authentication.getName());
        Doctor doctor = doctorService.findById(doctorId);
        Department department = departmentService.findById(departmentId);
        
        LocalDateTime appointTime = LocalDateTime.parse(appointmentTime);
        LocalDateTime now = LocalDateTime.now();
        
        if (appointTime.isBefore(now)) {
            return "redirect:/patient/book?error=past_time";
        }
        
        Map<String, Object> scheduleResult = doctorService.getScheduleList(doctorId);
        @SuppressWarnings("unchecked")
        List<Object> scheduleData = (List<Object>) scheduleResult.get("data");
        
        boolean validSchedule = false;
        if (scheduleData != null) {
            for (Object schObj : scheduleData) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sch = (Map<String, Object>) schObj;
                LocalDateTime startTime = (LocalDateTime) sch.get("startTime");
                LocalDateTime endTime = (LocalDateTime) sch.get("endTime");
                Integer remainNumber = (Integer) sch.get("remainNumber");
                Integer status = (Integer) sch.get("status");
                
                if (startTime != null && endTime != null && remainNumber != null && status != null &&
                    status == 1 &&
                    !appointTime.isBefore(startTime) && !appointTime.isAfter(endTime) && remainNumber > 0) {
                    validSchedule = true;
                    break;
                }
            }
        }
        
        if (!validSchedule) {
            return "redirect:/patient/book?error=invalid_time";
        }
        
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setDepartment(department);
        appointment.setAppointmentTime(appointTime);
        appointment.setStatus("已预约");
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setSymptoms(symptoms);
        
        appointmentService.saveAppointment(appointment);
        
        doctorService.decreaseScheduleRemainNumber(doctorId, appointTime);
        
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