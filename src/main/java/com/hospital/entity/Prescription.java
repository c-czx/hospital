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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String drugList;

    @Column(name = "usage_info")
    private String usage;
    
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