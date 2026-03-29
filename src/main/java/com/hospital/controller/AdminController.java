package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long userCount = userService.findAll().size();
        long departmentCount = departmentService.findAll().size();
        long doctorCount = doctorService.findAll().size();
        long appointmentCount = appointmentService.findAll().size();
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("departmentCount", departmentCount);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("appointmentCount", appointmentCount);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/user/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }
    
    @PostMapping("/user/create")
    public String createUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user-form";
    }
    
    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        user.setId(id);
        userService.updateUser(user);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    
    @GetMapping("/departments")
    public String departments(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "admin/departments";
    }
    
    @GetMapping("/department/create")
    public String createDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "admin/department-form";
    }
    
    @PostMapping("/department/create")
    public String createDepartment(@ModelAttribute Department department) {
        departmentService.saveDepartment(department);
        return "redirect:/admin/departments";
    }
    
    @GetMapping("/department/edit/{id}")
    public String editDepartment(@PathVariable Long id, Model model) {
        Department department = departmentService.findById(id);
        model.addAttribute("department", department);
        return "admin/department-form";
    }
    
    @PostMapping("/department/edit/{id}")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return "redirect:/admin/departments";
    }
    
    @GetMapping("/department/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }
    
    @GetMapping("/doctors")
    public String doctors(Model model) {
        List<Doctor> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        return "admin/doctors";
    }
    
    @GetMapping("/doctor/create")
    public String createDoctorForm(Model model) {
        List<Department> departments = departmentService.findAll();
        List<User> users = userService.findByRole("doctor");
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("departments", departments);
        model.addAttribute("users", users);
        return "admin/doctor-form";
    }
    
    @PostMapping("/doctor/create")
    public String createDoctor(@RequestParam Long userId, @RequestParam Long departmentId,
                           @RequestParam String title, @RequestParam String specialty,
                           @RequestParam String schedule) {
        User user = userService.findById(userId);
        Department department = departmentService.findById(departmentId);
        
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(department);
        doctor.setTitle(title);
        doctor.setSpecialty(specialty);
        doctor.setSchedule(schedule);
        
        doctorService.saveDoctor(doctor);
        return "redirect:/admin/doctors";
    }
    
    @GetMapping("/doctor/edit/{id}")
    public String editDoctor(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.findById(id);
        List<Department> departments = departmentService.findAll();
        List<User> users = userService.findByRole("doctor");
        model.addAttribute("doctor", doctor);
        model.addAttribute("departments", departments);
        model.addAttribute("users", users);
        return "admin/doctor-form";
    }
    
    @PostMapping("/doctor/edit/{id}")
    public String updateDoctor(@PathVariable Long id, @RequestParam Long userId, @RequestParam Long departmentId,
                           @RequestParam String title, @RequestParam String specialty,
                           @RequestParam String schedule) {
        User user = userService.findById(userId);
        Department department = departmentService.findById(departmentId);
        
        Doctor doctor = doctorService.findById(id);
        doctor.setUser(user);
        doctor.setDepartment(department);
        doctor.setTitle(title);
        doctor.setSpecialty(specialty);
        doctor.setSchedule(schedule);
        
        doctorService.updateDoctor(doctor);
        return "redirect:/admin/doctors";
    }
    
    @GetMapping("/doctor/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/admin/doctors";
    }
    
    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        return "admin/appointments";
    }
}