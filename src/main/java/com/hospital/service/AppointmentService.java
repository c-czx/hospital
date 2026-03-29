package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public Appointment saveAppointment(Appointment appointment) {
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setStatus("已预约");
        return appointmentRepository.save(appointment);
    }
    
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }
    
    public List<Appointment> findByUserId(Long userId) {
        return appointmentRepository.findByUserId(userId);
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
    
    public List<Appointment> findByAppointmentTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return appointmentRepository.findByAppointmentTimeBetween(startTime, endTime);
    }
    
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }
    
    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}