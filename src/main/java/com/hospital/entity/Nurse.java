package com.hospital.entity;

import jakarta.persistence.*;

/**
 * 护士实体类
 * 用于表示医院系统中的护士信息
 */
@Entity
@Table(name = "nurses")
public class Nurse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department; // 所属科室

    @Column(name = "ward", length = 50)
    private String ward; // 负责病区

    @Column(name = "title", length = 50)
    private String title; // 职称

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}