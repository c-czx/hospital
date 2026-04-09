package com.hospital.controller;

import com.hospital.entity.Advice;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Prescription;
import com.hospital.entity.Schedule;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.entity.Appointment;
import com.hospital.entity.Department;
import com.hospital.entity.Checkup;
import com.hospital.service.DoctorService;
import com.hospital.service.UserService;
import com.hospital.service.AdviceService;
import com.hospital.service.PrescriptionService;
import com.hospital.service.AppointmentService;
import com.hospital.service.DepartmentService;
import com.hospital.service.CheckupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AdviceService adviceService;
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private CheckupService checkupService;
    
    private Long getCurrentDoctorId(Authentication authentication) {
        try {
            String phone = authentication.getName();
            System.out.println("【调试】获取当前医生ID，电话：" + phone);
            
            User user = userService.findByPhone(phone);
            System.out.println("【调试】用户信息：" + (user != null ? user.getId() + " - " + user.getName() : "null"));
            
            if (user == null) {
                System.out.println("【调试】用户不存在，返回null");
                return null;
            }
            
            Doctor doctor = doctorService.findByUserId(user.getId());
            System.out.println("【调试】医生信息：" + (doctor != null ? doctor.getId() + " - " + doctor.getTitle() : "null"));
            
            return doctor != null ? doctor.getId() : null;
        } catch (Exception e) {
            System.out.println("【调试】获取医生ID异常：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        
        Map<String, Object> result = doctorService.getTodayPatient(doctorId);
        @SuppressWarnings("unchecked")
        List<Object> todayList = (List<Object>) result.get("data");
        
        model.addAttribute("todayList", todayList);
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
        model.addAttribute("doctorId", doctorId);
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
        List<Department> departments = departmentService.findAll();
        model.addAttribute("doctor", doctor);
        model.addAttribute("departments", departments);
        return "doctor/profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name, @RequestParam String gender,
                               @RequestParam Integer age, @RequestParam String phone,
                               @RequestParam String email, @RequestParam String title,
                               @RequestParam Long departmentId,
                               @RequestParam String schedule,
                               Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        
        Doctor doctor = doctorService.findById(doctorId);
        User user = doctor.getUser();
        Department department = departmentService.findById(departmentId);
        
        user.setName(name);
        user.setGender(gender);
        user.setAge(age);
        user.setPhone(phone);
        user.setEmail(email);
        userService.updateUser(user);
        
        doctor.setTitle(title);
        doctor.setDepartment(department);
        doctor.setSpecialty(department.getName());
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

    @GetMapping("/patients")
    public String myPatients(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("allAppointments", doctorService.getAllAppointments(doctorId).get("data"));
        return "doctor/appointments";
    }

    @GetMapping("/prescriptions")
    public String prescriptions(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        List<Prescription> prescriptions = prescriptionService.findByDoctorId(doctorId);
        model.addAttribute("prescriptions", prescriptions);
        return "doctor/advices";
    }

    @PostMapping("/publishSchedule")
    public String publishSchedule(@RequestParam String scheduleDate, 
                                  @RequestParam String startHour,
                                  @RequestParam String endHour,
                                  @RequestParam Integer totalNumber,
                                  Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        
        Schedule schedule = new Schedule();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        schedule.setDoctor(doctor);
        
        // 确保小时部分是两位数
        String startTime = scheduleDate + "T" + (startHour.length() == 1 ? "0" + startHour : startHour) + ":00:00";
        String endTime = scheduleDate + "T" + (endHour.length() == 1 ? "0" + endHour : endHour) + ":00:00";
        
        schedule.setStartTime(java.time.LocalDateTime.parse(startTime));
        schedule.setEndTime(java.time.LocalDateTime.parse(endTime));
        schedule.setTotalNumber(totalNumber);
        schedule.setRemainNumber(totalNumber);
        schedule.setStatus(1);
        
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
    public String createPrescription(Prescription prescription, @RequestParam Long doctorId, @RequestParam Long patientId, Authentication authentication) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        prescription.setDoctor(doctor);
        
        User user = new User();
        user.setId(patientId);
        prescription.setUser(user);
        
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
    
    @GetMapping("/checkup-form")
    public String checkupForm(@RequestParam Long patientId, Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("patientId", patientId);
        model.addAttribute("doctorId", doctorId);
        return "doctor/checkup-form";
    }
    
    @PostMapping("/createCheckup")
    public String createCheckup(Checkup checkup, @RequestParam Long doctorId, @RequestParam Long patientId, Authentication authentication) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        checkup.setDoctor(doctor);
        
        User user = new User();
        user.setId(patientId);
        checkup.setUser(user);
        
        checkupService.save(checkup);
        return "redirect:/doctor/dashboard";
    }
    
    @GetMapping("/checkups")
    public String checkups(Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        model.addAttribute("checkups", checkupService.getCheckupsWithPatientName(doctorId));
        return "doctor/checkups";
    }
    
    @GetMapping("/checkup-detail")
    public String checkupDetail(@RequestParam Long id, Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        Checkup checkup = checkupService.findById(id);
        model.addAttribute("checkup", checkup);
        return "doctor/checkup-detail";
    }
    
    @GetMapping("/update-checkup")
    public String updateCheckup(@RequestParam Long id, Model model, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        Checkup checkup = checkupService.findById(id);
        model.addAttribute("checkup", checkup);
        return "doctor/checkup-update";
    }
    
    @PostMapping("/update-checkup")
    public String updateCheckup(Checkup checkup, Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        if (doctorId == null) {
            return "redirect:/login";
        }
        checkupService.save(checkup);
        return "redirect:/doctor/checkups";
    }
}