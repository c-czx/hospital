package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PatientService patientService;
    
    public Appointment saveAppointment(Appointment appointment) {
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setStatus("已预约");
        return appointmentRepository.save(appointment);
    }
    
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }
    
    public List<Appointment> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> findByUserId(Long userId) {
        // 根据 userId 找到对应的 patientId，然后调用 findByPatientId 方法
        Patient patient = patientService.findByUserId(userId);
        if (patient != null) {
            return findByPatientId(patient.getId());
        }
        return java.util.Collections.emptyList();
    }
    
    public List<Appointment> findByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    
    public List<Appointment> findByDepartmentId(Long departmentId) {
        return appointmentRepository.findByDepartmentId(departmentId);
    }
    
    public List<Appointment> findByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }
    
    public List<Appointment> findByPatientIdAndStatus(Long patientId, String status) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, status);
    }
    
    public List<Appointment> findByUserIdAndStatus(Long userId, String status) {
        // 根据 userId 找到对应的 patientId，然后调用 findByPatientIdAndStatus 方法
        Patient patient = patientService.findByUserId(userId);
        if (patient != null) {
            return findByPatientIdAndStatus(patient.getId(), status);
        }
        return java.util.Collections.emptyList();
    }
    
    public List<Appointment> findByAppointmentTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.findByAppointmentTimeBetween(startTime, endTime);
    }
    
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
    
    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    public Appointment updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = findById(appointmentId);
        if (appointment != null) {
            appointment.setStatus(status);
            return appointmentRepository.save(appointment);
        }
        return null;
    }
    
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}