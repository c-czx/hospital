package com.hospital.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "nurses")
public class Nurse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nurse_id")
    private Integer nurseId;

    @Column(name = "user_id", unique = true, nullable = false)
    private Integer userId;

    @Column(name = "nurse_name", nullable = false, length = 50)
    private String nurseName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "department", length = 50)
    private String department;

    // Getters and Setters
    public Integer getNurseId() { return nurseId; }
    public void setNurseId(Integer nurseId) { this.nurseId = nurseId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getNurseName() { return nurseName; }
    public void setNurseName(String nurseName) { this.nurseName = nurseName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}