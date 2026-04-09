package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 检查项目实体类
 */
@Entity
@Table(name = "checkups")
public class Checkup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String type; // 检查类型：体温、血压、胸片等
    private String description; // 检查描述
    private String result; // 检查结果
    private Integer status; // 状态：0-待检查，1-已完成
    private LocalDateTime createTime;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
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
    
    // 用于接收表单提交的参数
    public void setUserId(Long userId) {
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            this.user = user;
        }
    }
    
    // 用于接收表单提交的参数
    public void setDoctorId(Long doctorId) {
        if (doctorId != null) {
            Doctor doctor = new Doctor();
            doctor.setId(doctorId);
            this.doctor = doctor;
        }
    }
}