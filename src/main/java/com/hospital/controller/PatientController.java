package com.hospital.controller;

import com.hospital.entity.*;
import com.hospital.service.*;
import com.hospital.repository.*;
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
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByPhone(authentication.getName());
        model.addAttribute("user", user);
        return "patient/dashboard";
    }
    

    
    @GetMapping("/book")
    public String bookAppointment(Model model, @RequestParam(required = false) Long doctorId, @RequestParam(required = false) Long departmentId) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("selectedDoctorId", doctorId);
        
        return "patient/book";
    }
    
    @GetMapping("/doctors")
    @ResponseBody
    public List<Map<String, Object>> getDoctors(@RequestParam Long departmentId) {
        List<Doctor> doctors = doctorService.findByDepartmentId(departmentId);
        List<Map<String, Object>> doctorList = new java.util.ArrayList<>();
        
        if (doctors != null) {
            for (Doctor doctor : doctors) {
                Map<String, Object> doctorMap = new java.util.HashMap<>();
                doctorMap.put("id", doctor.getId());
                
                if (doctor.getUser() != null) {
                    Map<String, Object> userMap = new java.util.HashMap<>();
                    userMap.put("id", doctor.getUser().getId());
                    userMap.put("name", doctor.getUser().getName());
                    doctorMap.put("user", userMap);
                }
                
                doctorMap.put("title", doctor.getTitle());
                doctorMap.put("specialty", doctor.getSpecialty());
                
                doctorList.add(doctorMap);
            }
        }
        
        return doctorList;
    }
    
    @GetMapping("/schedules")
    @ResponseBody
    public List<Object> getSchedules(@RequestParam Long doctorId) {
        Map<String, Object> scheduleResult = doctorService.getScheduleList(doctorId);
        @SuppressWarnings("unchecked")
        List<Object> scheduleData = (List<Object>) scheduleResult.get("data");
        return scheduleData;
    }
    
    @PostMapping("/book")
    public String bookAppointment(@RequestParam Long doctorId, @RequestParam Long departmentId,
                                  @RequestParam String appointmentTime, @RequestParam String symptoms,
                                  Authentication authentication) {
        
        System.out.println("【预约挂号】开始处理预约请求");
        System.out.println("【预约挂号】医生ID: " + doctorId);
        System.out.println("【预约挂号】科室ID: " + departmentId);
        System.out.println("【预约挂号】预约时间: " + appointmentTime);
        System.out.println("【预约挂号】症状描述: " + symptoms);
        
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            System.out.println("【预约挂号】用户不存在");
            return "redirect:/patient/book?error=user_not_found";
        }
        System.out.println("【预约挂号】用户ID: " + user.getId() + ", 用户名: " + user.getName());
        
        Doctor doctor = doctorService.findById(doctorId);
        if (doctor == null) {
            System.out.println("【预约挂号】医生不存在");
            return "redirect:/patient/book?error=doctor_not_found";
        }
        System.out.println("【预约挂号】医生信息: " + doctor.getId() + ", " + doctor.getUser().getName());
        
        Department department = departmentService.findById(departmentId);
        if (department == null) {
            System.out.println("【预约挂号】科室不存在");
            return "redirect:/patient/book?error=department_not_found";
        }
        System.out.println("【预约挂号】科室信息: " + department.getId() + ", " + department.getName());
        
        // 解析不同格式的日期时间字符串
        LocalDateTime appointTime;
        try {
            // 尝试解析包含秒的格式：2026-04-09T08:00:00
            appointTime = LocalDateTime.parse(appointmentTime);
            System.out.println("【预约挂号】解析日期时间成功: " + appointTime);
        } catch (Exception e) {
            try {
                // 尝试解析不包含秒的格式：2026-04-09T08:00
                appointTime = LocalDateTime.parse(appointmentTime + ":00");
                System.out.println("【预约挂号】解析日期时间成功: " + appointTime);
            } catch (Exception ex) {
                System.out.println("【预约挂号】解析日期时间失败: " + ex.getMessage());
                return "redirect:/patient/book?error=invalid_time_format";
            }
        }
        LocalDateTime now = LocalDateTime.now();
        
        // 检查预约时间是否在排班的时间范围内
        boolean isWithinSchedule = false;
        Map<String, Object> scheduleResult = doctorService.getScheduleList(doctorId);
        @SuppressWarnings("unchecked")
        List<Object> schedules = (List<Object>) scheduleResult.get("data");
        if (schedules != null) {
            for (Object obj : schedules) {
                Map<String, Object> schMap = (Map<String, Object>) obj;
                LocalDateTime startTime = null;
                LocalDateTime endTime = null;
                
                // 处理日期时间字符串转换
                if (schMap.get("startTime") != null) {
                    if (schMap.get("startTime") instanceof String) {
                        try {
                            startTime = LocalDateTime.parse((String) schMap.get("startTime"));
                        } catch (Exception e) {
                            System.out.println("【预约挂号】解析 startTime 失败: " + e.getMessage());
                        }
                    } else if (schMap.get("startTime") instanceof LocalDateTime) {
                        startTime = (LocalDateTime) schMap.get("startTime");
                    }
                }
                
                if (schMap.get("endTime") != null) {
                    if (schMap.get("endTime") instanceof String) {
                        try {
                            endTime = LocalDateTime.parse((String) schMap.get("endTime"));
                        } catch (Exception e) {
                            System.out.println("【预约挂号】解析 endTime 失败: " + e.getMessage());
                        }
                    } else if (schMap.get("endTime") instanceof LocalDateTime) {
                        endTime = (LocalDateTime) schMap.get("endTime");
                    }
                }
                
                if (startTime != null && endTime != null) {
                    if (!appointTime.isBefore(startTime) && !appointTime.isAfter(endTime)) {
                        // 检查当前时间是否在排班的结束时间之前
                        if (now.isBefore(endTime)) {
                            isWithinSchedule = true;
                            break;
                        }
                    }
                }
            }
        }
        
        if (!isWithinSchedule) {
            System.out.println("【预约挂号】预约时间不在有效的排班时间范围内: " + appointTime);
            return "redirect:/patient/book?error=invalid_time&message=预约时间不在有效的排班时间范围内";
        }
        
        // 直接创建预约，跳过号源验证
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setDepartment(department);
        appointment.setAppointmentTime(appointTime);
        appointment.setStatus("已预约");
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setSymptoms(symptoms);
        
        try {
            Appointment savedAppointment = appointmentService.saveAppointment(appointment);
            System.out.println("【预约挂号】保存预约成功，预约ID: " + savedAppointment.getId());
        } catch (Exception e) {
            System.out.println("【预约挂号】保存预约失败: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/patient/book?error=save_failed";
        }
        
        // 尝试减少号源数量
        try {
            doctorService.decreaseScheduleRemainNumber(doctorId, appointTime);
            System.out.println("【预约挂号】减少号源数量成功");
        } catch (Exception e) {
            System.out.println("【预约挂号】减少号源数量失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 测试获取预约列表
        List<Appointment> appointments = appointmentService.findByUserId(user.getId());
        System.out.println("【预约挂号】用户预约列表数量: " + appointments.size());
        for (Appointment app : appointments) {
            System.out.println("【预约挂号】预约记录: " + app.getId() + ", " + app.getDoctor().getUser().getName() + ", " + app.getAppointmentTime());
        }
        
        return "redirect:/patient/appointments";
    }
    
    @GetMapping("/departments")
    public String departments(Model model) {
        List<Department> departments = departmentService.findAll();
        for (Department dept : departments) {
            List<Doctor> doctors = doctorService.findByDepartmentId(dept.getId());
            dept.setDoctors(doctors);
        }
        model.addAttribute("departments", departments);
        return "patient/departments";
    }
    
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        // 尝试通过电话号码查找用户
        User user = userService.findByPhone(authentication.getName());
        
        // 如果找不到用户，可能是因为用户修改了电话号码
        // 这里可以通过其他方式查找用户，比如从数据库中获取所有用户并匹配
        // 为了简化，我们暂时使用重定向到登录页面的方式
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "patient/profile";
    }
    
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name, @RequestParam String gender,
                               @RequestParam String phone, @RequestParam String email,
                               Authentication authentication) {
        // 首先使用旧电话号码查找用户
        User user = userService.findByPhone(authentication.getName());
        if (user != null) {
            // 检查电话号码是否变更
            boolean phoneChanged = !phone.equals(user.getPhone());
            
            // 检查新电话号码是否已经被其他用户使用
            if (phoneChanged && userService.existsByPhone(phone)) {
                // 电话号码已被使用，重定向回个人信息页面并显示错误信息
                return "redirect:/patient/profile?error=phone_exists";
            }
            
            // 更新用户信息
            user.setPhone(phone);
            user.setName(name);
            user.setGender(gender);
            user.setEmail(email);
            userService.updateUser(user);
            
            // 如果电话号码变更，重定向到登录页面，让用户使用新的电话号码重新登录
            if (phoneChanged) {
                return "redirect:/login?message=phone_updated&success=1";
            }
            
            // 如果电话号码没有变更，重定向回个人信息页面并显示成功信息
            return "redirect:/patient/profile?success=1";
        }
        
        return "redirect:/patient/profile";
    }
    

    
    @GetMapping("/appointments")
    public String appointments(Authentication authentication, Model model) {
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        List<Appointment> appointments = appointmentService.findByUserId(user.getId());
        model.addAttribute("appointments", appointments);
        return "patient/appointments";
    }
    
    @Autowired
    private BillingService billingService;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private AdviceRepository adviceRepository;
    
    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取已就诊记录（状态为已完成的预约）
        List<Appointment> completedAppointments = appointmentService.findByUserIdAndStatus(user.getId(), "已完成");
        
        // 获取缴费记录
        List<Billing> billingRecords = billingService.findByUserId(user.getId());
        
        // 获取病历记录
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findByUserId(user.getId());
        
        // 获取处方记录
        List<Prescription> prescriptions = prescriptionRepository.findByUserId(user.getId());
        
        // 获取医嘱记录
        List<Advice> advices = adviceRepository.findByUserId(user.getId());
        
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("billingRecords", billingRecords);
        model.addAttribute("medicalRecords", medicalRecords);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("advices", advices);
        return "patient/orders";
    }
    
    @PostMapping("/pay-billing")
    public String payBilling(@RequestParam Long billingId, Authentication authentication) {
        User user = userService.findByPhone(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取缴费记录
        Billing billing = billingService.findById(billingId);
        if (billing != null && billing.getUser().getId().equals(user.getId())) {
            // 更新缴费状态为已支付
            billing.setStatus("已支付");
            billingService.updateBilling(billing);
        }
        
        return "redirect:/patient/orders";
    }
}