package com.hospital.service;

import com.hospital.entity.Doctor;
import com.hospital.entity.Advice;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Prescription;
import com.hospital.entity.Schedule;
import com.hospital.entity.User;
import com.hospital.entity.Appointment;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.AdviceRepository;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.UserRepository;
import com.hospital.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return doctorRepository.findByDepartmentId(departmentId);
    }

    public Doctor findByUserId(Long userId) {
        List<Doctor> doctors = doctorRepository.findByUserId(userId);
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // 【今日预约患者】
    public Map<String, Object> getTodayPatient(Long doctorId) {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, todayStart, todayEnd);
        
        List<Object> data = new ArrayList<>();
        for (Appointment apt : appointments) {
            if ("已预约".equals(apt.getStatus())) {
                Map<String, Object> patientMap = new HashMap<>();
                User patient = apt.getUser();
                patientMap.put("id", patient.getId());
                patientMap.put("name", patient.getName());
                patientMap.put("gender", patient.getGender());
                patientMap.put("age", patient.getAge());
                patientMap.put("phone", patient.getPhone());
                patientMap.put("appointmentId", apt.getId());
                patientMap.put("appointmentTime", apt.getAppointmentTime());
                data.add(patientMap);
            }
        }
        return Map.of("code", 200, "data", data);
    }

    // 【全部预约】
    public Map<String, Object> getAllAppointments(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        
        List<Object> data = new ArrayList<>();
        for (Appointment apt : appointments) {
            Map<String, Object> aptMap = new HashMap<>();
            User patient = apt.getUser();
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
        return Map.of("code", 200, "data", data);
    }

    // 【患者详情】
    public Map<String, Object> getPatientDetail(Long patientId) {
        User patient = userRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return Map.of("code", 404, "msg", "患者不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", patient.getId());
        data.put("name", patient.getName());
        data.put("gender", patient.getGender());
        data.put("age", patient.getAge());
        data.put("phone", patient.getPhone());
        data.put("email", patient.getEmail());
        
        return Map.of("code", 200, "data", data);
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
        return Map.of("code", 200, "data", data);
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
        return Map.of("code", 200, "data", data);
    }

    // 【发布号源】
    public Map<String, Object> publishSchedule(Schedule schedule) {
        schedule.setStatus(1);
        schedule.setRemainNumber(schedule.getTotalNumber());
        scheduleRepository.save(schedule);
        return Map.of("code", 200, "msg", "发布成功");
    }

    // 【保存病历】
    public Map<String, Object> saveRecord(MedicalRecord record) {
        record.setCreateTime(LocalDateTime.now());
        record.setDiagnosisTime(LocalDateTime.now());
        medicalRecordRepository.save(record);
        return Map.of("code", 200, "msg", "保存成功");
    }

    // 【开具处方】
    public Map<String, Object> createPrescription(Prescription prescription) {
        prescription.setCreateTime(LocalDateTime.now());
        prescriptionRepository.save(prescription);
        return Map.of("code", 200, "msg", "开具成功");
    }

    // 【修改医嘱】
    public Map<String, Object> updateAdvice(Advice advice) {
        Advice existingAdvice = adviceRepository.findById(advice.getId()).orElse(null);
        if (existingAdvice == null) {
            return Map.of("code", 404, "msg", "医嘱不存在");
        }
        
        existingAdvice.setContent(advice.getContent());
        existingAdvice.setStatus(advice.getStatus());
        existingAdvice.setUpdateTime(LocalDateTime.now());
        adviceRepository.save(existingAdvice);
        return Map.of("code", 200, "msg", "修改成功");
    }
    
    // 【新增】创建医嘱
    public Map<String, Object> createAdvice(Advice advice) {
        advice.setCreateTime(LocalDateTime.now());
        advice.setStatus(1);
        adviceRepository.save(advice);
        return Map.of("code", 200, "msg", "创建成功");
    }
}