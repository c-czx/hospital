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

    @Column(name = "phone", length = 20)
    private String phone;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}