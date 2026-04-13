package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 处方实体类
 * 用于表示医生为患者开具的处方信息，包括药品清单、用法用量等
 */
@Entity
@Table(name = "prescriptions")
public class Prescription {
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
    
    private String drugList;

    @Column(name = "usage_info")
    private String usage;
    
    @Column(columnDefinition = "DATETIME(0)")
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
    
    public String getDrugList() { 
        return drugList; 
    }
    
    public void setDrugList(String drugList) { 
        this.drugList = drugList; 
    }
    
    public String getUsage() { 
        return usage; 
    }
    
    public void setUsage(String usage) { 
        this.usage = usage; 
    }
    
    public LocalDateTime getCreateTime() { 
        return createTime; 
    }
    
    public void setCreateTime(LocalDateTime createTime) { 
        this.createTime = createTime; 
    }
}