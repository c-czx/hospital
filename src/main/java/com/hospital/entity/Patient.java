package com.hospital.entity;

import jakarta.persistence.*;

/**
 * 患者实体类
 * 用于表示医院系统中的患者信息，包括病历号、过敏史、紧急联系人等
 */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联用户表，确保这个患者对应系统中的一个用户账号
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "medical_record_number", unique = true)
    private String medicalRecordNumber; // 病历号

    @Column(name = "allergies")
    private String allergies; // 过敏药物

    @Column(name = "emergency_contact")
    private String emergencyContact; // 紧急联系人

    @Column(name = "emergency_phone")
    private String emergencyPhone; // 联系电话

    // Constructors
    public Patient() {}

    public Patient(User user) {
        this.user = user;
        this.medicalRecordNumber = "MR" + System.currentTimeMillis(); // 简单生成病历号
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }
}