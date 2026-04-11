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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String chiefComplaint;
    private String presentIllness;
    private String diagnosisResult;
    private LocalDateTime diagnosisTime;
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
    
    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) { 
        this.user = user; 
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