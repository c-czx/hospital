package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 病历实体类
 * 用于表示患者的病历记录，包括主诉、现病史、诊断结果等
 */
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    private String chiefComplaint;
    private String presentIllness;
    private String diagnosisResult;
    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime diagnosisTime;
    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime createTime;
    private Double temperature;
    private Integer bloodPressure;
    private String nurseNotes;

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public Doctor getDoctor() { 
        return doctor; 
    }
    
    public void setDoctor(Doctor doctor) { 
        this.doctor = doctor; 
    }
    
    public Patient getPatient() { 
        return patient; 
    }
    
    public void setPatient(Patient patient) { 
        this.patient = patient; 
    }
    
    public Appointment getAppointment() {
        return appointment;
    }
    
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
    
    public String getChiefComplaint() { 
        return chiefComplaint; 
    }
    
    public void setChiefComplaint(String chiefComplaint) { 
        this.chiefComplaint = chiefComplaint; 
    }
    
    public String getPresentIllness() { 
        return presentIllness; 
    }
    
    public void setPresentIllness(String presentIllness) { 
        this.presentIllness = presentIllness; 
    }
    
    public String getDiagnosisResult() { 
        return diagnosisResult; 
    }
    
    public void setDiagnosisResult(String diagnosisResult) { 
        this.diagnosisResult = diagnosisResult; 
    }
    
    public LocalDateTime getDiagnosisTime() { 
        return diagnosisTime; 
    }
    
    public void setDiagnosisTime(LocalDateTime diagnosisTime) { 
        this.diagnosisTime = diagnosisTime; 
    }
    
    public LocalDateTime getCreateTime() { 
        return createTime; 
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Integer getBloodPressure() {
        return bloodPressure;
    }
    
    public void setBloodPressure(Integer bloodPressure) {
        this.bloodPressure = bloodPressure;
    }
    
    public String getNurseNotes() {
        return nurseNotes;
    }
    
    public void setNurseNotes(String nurseNotes) {
        this.nurseNotes = nurseNotes;
    }
}