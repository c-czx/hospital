package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 留言实体类
 * 用于表示患者给医生的留言信息，包括留言内容、状态等
 */
@Entity
@Table(name = "advices")
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    private String content;
    private Integer status;
    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime createTime;
    @Column(columnDefinition = "DATETIME(0)")
    private LocalDateTime updateTime;

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
    
    public String getContent() { 
        return content; 
    }
    
    public void setContent(String content) { 
        this.content = content; 
    }
    
    public Integer getStatus() { 
        return status; 
    }
    
    public void setStatus(Integer status) { 
        this.status = status; 
    }
    
    public LocalDateTime getCreateTime() { 
        return createTime; 
    }
    
    public void setCreateTime(LocalDateTime createTime) { 
        this.createTime = createTime; 
    }
    
    public LocalDateTime getUpdateTime() { 
        return updateTime; 
    }
    
    public void setUpdateTime(LocalDateTime updateTime) { 
        this.updateTime = updateTime; 
    }
}