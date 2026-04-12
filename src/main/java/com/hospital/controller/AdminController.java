package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 * 提供管理员端的用户管理、科室管理、医生管理、预约管理等功能
 */
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
    
    @Autowired
    private NurseService nurseService;
    
    @Autowired
    private PatientService patientService;
    
    /**
     * 显示管理员首页
     */
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
    
    /**
     * 显示用户列表
     */
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    /**
     * 显示添加用户表单
     */
    @GetMapping("/user/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }
    
    /**
     * 处理添加用户请求
     */
    @PostMapping("/user/create")
    public String createUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
    
    /**
     * 显示编辑用户表单
     */
    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user-form";
    }
    
    /**
     * 处理编辑用户请求
     */
    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        User existingUser = userService.findById(id);
        if (existingUser != null) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
            user.setId(id);
            userService.updateUser(user);
        }
        return "redirect:/admin/users";
    }
    
    /**
     * 删除用户
     */
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    
    /**
     * 更新用户角色
     */
    @GetMapping("/user/update-role/{id}")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role) {
        User user = userService.findById(id);
        if (user != null) {
            user.setRole(role);
            userService.updateUser(user);
        }
        return "redirect:/admin/users";
    }
    
    /**
     * 显示科室列表
     */
    @GetMapping("/departments")
    public String departments(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "admin/departments";
    }
    
    /**
     * 显示添加科室表单
     */
    @GetMapping("/department/create")
    public String createDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "admin/department-form";
    }
    
    /**
     * 处理添加科室请求
     */
    @PostMapping("/department/create")
    public String createDepartment(@ModelAttribute Department department) {
        departmentService.saveDepartment(department);
        return "redirect:/admin/departments";
    }
    
    /**
     * 显示编辑科室表单
     */
    @GetMapping("/department/edit/{id}")
    public String editDepartment(@PathVariable Long id, Model model) {
        Department department = departmentService.findById(id);
        model.addAttribute("department", department);
        return "admin/department-form";
    }
    
    /**
     * 处理编辑科室请求
     */
    @PostMapping("/department/edit/{id}")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return "redirect:/admin/departments";
    }
    
    /**
     * 删除科室
     */
    @GetMapping("/department/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }
    
    /**
     * 显示医生列表
     */
    @GetMapping("/doctors")
    public String doctors(Model model) {
        List<Doctor> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        return "admin/doctors";
    }
    
    /**
     * 显示添加医生表单
     */
    @GetMapping("/doctor/create")
    public String createDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        List<Department> departments = departmentService.findAll();
        // 获取所有非 DOCTOR 角色的用户
        List<User> allUsers = userService.findAll();
        List<User> availableUsers = allUsers.stream()
            .filter(u -> !"DOCTOR".equals(u.getRole()))
            .collect(java.util.stream.Collectors.toList());
        model.addAttribute("departments", departments);
        model.addAttribute("users", availableUsers);
        return "admin/doctor-form";
    }
    
    /**
     * 处理添加医生请求
     */
    @PostMapping("/doctor/create")
    public String createDoctor(@RequestParam Long userId, @RequestParam Long departmentId,
                           @RequestParam String title, @RequestParam String specialty,
                           @RequestParam String schedule) {
        User user = userService.findById(userId);
        // 同步更新用户角色为 DOCTOR
        user.setRole("DOCTOR");
        userService.updateUser(user);
        
        // 如果用户之前是患者，从患者表中删除
        Patient oldPatient = patientService.findByUser(user);
        if (oldPatient != null) {
            patientService.deletePatient(oldPatient.getId());
        }
        // 如果用户之前是护士，从护士表中删除
        Nurse oldNurse = nurseService.findByUser(user);
        if (oldNurse != null) {
            nurseService.deleteNurse(oldNurse.getId());
        }
        
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
    
    /**
     * 显示编辑医生表单
     */
    @GetMapping("/doctor/edit/{id}")
    public String editDoctor(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.findById(id);
        List<Department> departments = departmentService.findAll();
        List<User> users = userService.findByRole("DOCTOR");
        model.addAttribute("doctor", doctor);
        model.addAttribute("departments", departments);
        model.addAttribute("users", users);
        return "admin/doctor-form";
    }
    
    /**
     * 处理编辑医生请求
     */
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
    
    /**
     * 删除医生
     */
    @GetMapping("/doctor/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/admin/doctors";
    }
    
    /**
     * 显示预约列表
     */
    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentService.findAll();
        model.addAttribute("appointments", appointments);
        return "admin/appointments";
    }
    
    /**
     * 显示护士列表
     */
    @GetMapping("/nurses")
    public String nurses(Model model) {
        List<Nurse> nurses = nurseService.findAll();
        model.addAttribute("nurses", nurses);
        return "admin/nurses";
    }
    
    /**
     * 显示添加护士表单
     */
    @GetMapping("/nurse/create")
    public String createNurseForm(Model model) {
        model.addAttribute("nurse", new Nurse());
        // 获取所有非 NURSE 角色的用户
        List<User> allUsers = userService.findAll();
        List<User> availableUsers = allUsers.stream()
            .filter(u -> !"NURSE".equals(u.getRole()))
            .collect(java.util.stream.Collectors.toList());
        List<Department> departments = departmentService.findAll();
        model.addAttribute("users", availableUsers);
        model.addAttribute("departments", departments);
        return "admin/nurse-form";
    }
    
    /**
     * 处理添加护士请求
     */
    @PostMapping("/nurse/create")
    public String createNurse(@RequestParam Long userId, @RequestParam(required = false) Long departmentId,
                             @RequestParam(required = false) String ward, @RequestParam(required = false) String title) {
        User user = userService.findById(userId);
        // 同步更新用户角色为 NURSE
        user.setRole("NURSE");
        userService.updateUser(user);
        
        // 如果用户之前是患者，从患者表中删除
        Patient oldPatient = patientService.findByUser(user);
        if (oldPatient != null) {
            patientService.deletePatient(oldPatient.getId());
        }
        // 如果用户之前是医生，从医生表中删除
        Doctor oldDoctor = doctorService.findByUser(user);
        if (oldDoctor != null) {
            doctorService.deleteDoctor(oldDoctor);
        }
        
        Department department = null;
        if (departmentId != null && departmentId != 0) {
            department = departmentService.findById(departmentId);
        }
        
        Nurse nurse = new Nurse();
        nurse.setUser(user);
        nurse.setDepartment(department);
        nurse.setWard(ward);
        nurse.setTitle(title);
        
        nurseService.saveNurse(nurse);
        return "redirect:/admin/nurses";
    }
    
    /**
     * 显示编辑护士表单
     */
    @GetMapping("/nurse/edit/{id}")
    public String editNurse(@PathVariable Long id, Model model) {
        Nurse nurse = nurseService.findById(id);
        List<Department> departments = departmentService.findAll();
        model.addAttribute("nurse", nurse);
        model.addAttribute("departments", departments);
        return "admin/nurse-form";
    }
    
    /**
     * 处理编辑护士请求
     */
    @PostMapping("/nurse/edit/{id}")
    public String updateNurse(@PathVariable Long id, @RequestParam Long userId,
                             @RequestParam(required = false) Long departmentId,
                             @RequestParam(required = false) String ward, @RequestParam(required = false) String title) {
        User user = userService.findById(userId);
        
        Department department = null;
        if (departmentId != null && departmentId != 0) {
            department = departmentService.findById(departmentId);
        }
        
        Nurse nurse = nurseService.findById(id);
        nurse.setUser(user);
        nurse.setDepartment(department);
        nurse.setWard(ward);
        nurse.setTitle(title);
        
        nurseService.updateNurse(nurse);
        return "redirect:/admin/nurses";
    }
    
    /**
     * 删除护士
     */
    @GetMapping("/nurse/delete/{id}")
    public String deleteNurse(@PathVariable Long id) {
        nurseService.deleteNurse(id);
        return "redirect:/admin/nurses";
    }
}
