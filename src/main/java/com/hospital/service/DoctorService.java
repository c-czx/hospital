package com.hospital.service;

import com.hospital.entity.Doctor;
import com.hospital.entity.Advice;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Prescription;
import com.hospital.entity.Schedule;
import com.hospital.entity.User;
import com.hospital.entity.Appointment;
import com.hospital.entity.Checkup;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.AdviceRepository;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.UserRepository;
import com.hospital.repository.ScheduleRepository;
import com.hospital.repository.CheckupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private AdviceRepository adviceRepository;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CheckupRepository checkupRepository;

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public List<Doctor> findByDepartmentId(Long departmentId) {
        List<Doctor> doctors = doctorRepository.findByDepartmentId(departmentId);
        
        // 确保 user 属性被加载
        if (doctors != null) {
            for (Doctor doctor : doctors) {
                if (doctor.getUser() != null) {
                    // 访问 user 属性，触发懒加载
                    doctor.getUser().getName();
                }
            }
        }
        return doctors;
    }

    public Doctor findByUserId(Long userId) {
        List<Doctor> doctors = doctorRepository.findByUserId(userId);
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    /**
     * 根据用户查找医生
     */
    public Doctor findByUser(User user) {
        List<Doctor> doctors = doctorRepository.findByUserId(user.getId());
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    /**
     * 删除医生
     */
    @Transactional
    public void deleteDoctor(Doctor doctor) {
        doctorRepository.delete(doctor);
    }

    // 【今日预约患者】
    public Map<String, Object> getTodayPatient(Long doctorId) {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, todayStart, todayEnd);
        
        List<Object> data = new ArrayList<>();
        if (appointments != null) {
            for (Appointment apt : appointments) {
                if ("已预约".equals(apt.getStatus()) || "叫号中".equals(apt.getStatus()) || "已完成".equals(apt.getStatus())) {
                    Map<String, Object> patientMap = new HashMap<>();
                    User patient = apt.getUser();
                    if (patient != null) {
                        patientMap.put("id", patient.getId());
                        patientMap.put("name", patient.getName());
                        patientMap.put("gender", patient.getGender());
                        patientMap.put("age", patient.getAge());
                        patientMap.put("phone", patient.getPhone());
                        patientMap.put("appointmentId", apt.getId());
                        patientMap.put("appointmentTime", apt.getAppointmentTime());
                        patientMap.put("status", apt.getStatus());
                        patientMap.put("symptoms", apt.getSymptoms());
                        data.add(patientMap);
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    // 【全部预约】
    public Map<String, Object> getAllAppointments(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        
        List<Object> data = new ArrayList<>();
        for (Appointment apt : appointments) {
            Map<String, Object> aptMap = new HashMap<>();
            User patient = apt.getUser();
            if (patient != null) {
                aptMap.put("id", patient.getId());
                aptMap.put("name", patient.getName());
                aptMap.put("gender", patient.getGender());
                aptMap.put("age", patient.getAge());
                aptMap.put("phone", patient.getPhone());
                aptMap.put("appointmentId", apt.getId());
                aptMap.put("appointmentTime", apt.getAppointmentTime());
                aptMap.put("status", apt.getStatus());
                aptMap.put("symptoms", apt.getSymptoms());
                data.add(aptMap);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    // 【患者详情】
    public Map<String, Object> getPatientDetail(Long patientId) {
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 404);
            errorResult.put("msg", "患者不存在");
            return errorResult;
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", patient.getId());
        data.put("name", patient.getName());
        data.put("gender", patient.getGender());
        data.put("age", patient.getAge());
        data.put("phone", patient.getPhone());
        data.put("email", patient.getEmail());
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    // 【医嘱列表】
    public Map<String, Object> getAdviceList(Long patientId) {
        List<Advice> advices = adviceRepository.findByUserId(patientId);
        
        List<Object> data = new ArrayList<>();
        for (Advice advice : advices) {
            Map<String, Object> adviceMap = new HashMap<>();
            adviceMap.put("id", advice.getId());
            adviceMap.put("content", advice.getContent());
            adviceMap.put("createTime", advice.getCreateTime());
            adviceMap.put("status", advice.getStatus());
            data.add(adviceMap);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }
    
    // 【获取患者病历】当前医生维度下时间最新的一条（与医生端详情/保存逻辑一致）
    public MedicalRecord getMedicalRecord(Long patientId, Long doctorId) {
        List<MedicalRecord> records = medicalRecordRepository.findByUserId(patientId);
        return records.stream()
                .filter(r -> r.getDoctor() != null && doctorId.equals(r.getDoctor().getId()))
                .max(Comparator.comparing(this::medicalRecordSortTime))
                .orElse(null);
    }

    private LocalDateTime medicalRecordSortTime(MedicalRecord r) {
        if (r.getCreateTime() != null) {
            return r.getCreateTime();
        }
        if (r.getDiagnosisTime() != null) {
            return r.getDiagnosisTime();
        }
        return LocalDateTime.MIN;
    }
    
    // 【获取患者处方】
    public Prescription getPrescription(Long patientId, Long doctorId) {
        List<Prescription> prescriptions = prescriptionRepository.findByUserId(patientId);
        return prescriptions.stream()
                .filter(p -> p.getDoctor() != null && doctorId.equals(p.getDoctor().getId()))
                .max(Comparator.comparing(p -> p.getCreateTime() != null ? p.getCreateTime() : LocalDateTime.MIN))
                .orElse(null);
    }
    
    // 【获取患者医嘱】
    public Advice getAdvice(Long patientId, Long doctorId) {
        List<Advice> advices = adviceRepository.findByUserId(patientId);
        return advices.stream()
                .filter(a -> a.getDoctor() != null && doctorId.equals(a.getDoctor().getId()))
                .max(Comparator.comparing(a -> a.getCreateTime() != null ? a.getCreateTime() : LocalDateTime.MIN))
                .orElse(null);
    }
    
    // 【获取患者检查记录】
    public Checkup getCheckup(Long patientId, Long doctorId) {
        List<Checkup> checkups = checkupRepository.findByUser_Id(patientId);
        return checkups.stream()
                .filter(c -> c.getDoctor() != null && doctorId.equals(c.getDoctor().getId()))
                .max(Comparator.comparing(c -> c.getCreateTime() != null ? c.getCreateTime() : LocalDateTime.MIN))
                .orElse(null);
    }
    
    // 【保存检查记录】
    public void saveCheckup(Checkup checkup) {
        checkupRepository.save(checkup);
    }

    // 【排班列表】
    public Map<String, Object> getScheduleList(Long doctorId) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        
        List<Object> data = new ArrayList<>();
        for (Schedule sch : schedules) {
            Map<String, Object> schMap = new HashMap<>();
            schMap.put("id", sch.getId());
            schMap.put("startTime", sch.getStartTime());
            schMap.put("endTime", sch.getEndTime());
            schMap.put("totalNumber", sch.getTotalNumber());
            schMap.put("remainNumber", sch.getRemainNumber());
            schMap.put("status", sch.getStatus());
            data.add(schMap);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    // 【发布号源】
    public Map<String, Object> publishSchedule(Schedule schedule) {
        schedule.setStatus(1);
        schedule.setRemainNumber(schedule.getTotalNumber());
        scheduleRepository.save(schedule);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "发布成功");
        return result;
    }

    // 【保存病历】
    public Map<String, Object> saveRecord(MedicalRecord record) {
        record.setCreateTime(LocalDateTime.now());
        record.setDiagnosisTime(LocalDateTime.now());
        medicalRecordRepository.save(record);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "保存成功");
        return result;
    }

    // 【开具处方】
    public Map<String, Object> createPrescription(Prescription prescription) {
        prescription.setCreateTime(LocalDateTime.now());
        prescriptionRepository.save(prescription);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "开具成功");
        return result;
    }

    // 【修改医嘱】
    public Map<String, Object> updateAdvice(Advice advice) {
        Advice existingAdvice = adviceRepository.findById(advice.getId()).orElse(null);
        if (existingAdvice == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("code", 404);
            errorResult.put("msg", "医嘱不存在");
            return errorResult;
        }
        
        existingAdvice.setContent(advice.getContent());
        existingAdvice.setStatus(advice.getStatus());
        existingAdvice.setUpdateTime(LocalDateTime.now());
        adviceRepository.save(existingAdvice);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "修改成功");
        return result;
    }
    
    // 【新增】创建医嘱
    public Map<String, Object> createAdvice(Advice advice) {
        // 检查是否已有医嘱
        List<Advice> existingAdvices = adviceRepository.findByUserId(advice.getUser().getId());
        if (!existingAdvices.isEmpty()) {
            // 更新现有医嘱
            Advice existingAdvice = existingAdvices.get(0);
            existingAdvice.setContent(advice.getContent());
            existingAdvice.setStatus(advice.getStatus());
            existingAdvice.setUpdateTime(LocalDateTime.now());
            adviceRepository.save(existingAdvice);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "更新成功");
            return result;
        } else {
            // 创建新医嘱
            advice.setCreateTime(LocalDateTime.now());
            advice.setStatus(1);
            adviceRepository.save(advice);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "创建成功");
            return result;
        }
    }

    // 【新增】减少号源剩余数量
    public void decreaseScheduleRemainNumber(Long doctorId, LocalDateTime appointmentTime) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        for (Schedule schedule : schedules) {
            if (!appointmentTime.isBefore(schedule.getStartTime()) && 
                !appointmentTime.isAfter(schedule.getEndTime()) &&
                schedule.getRemainNumber() > 0) {
                schedule.setRemainNumber(schedule.getRemainNumber() - 1);
                scheduleRepository.save(schedule);
                break;
            }
        }
    }
}