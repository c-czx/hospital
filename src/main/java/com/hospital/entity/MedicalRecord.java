package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
}