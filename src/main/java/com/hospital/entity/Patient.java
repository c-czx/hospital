package com.hospital.entity;

import jakarta.persistence.*;
        import java.time.LocalDateTime;

@Entity
@Table(name = "patients") // 对应数据库表名  患者
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

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

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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