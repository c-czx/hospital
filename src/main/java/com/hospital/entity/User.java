package com.hospital.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String role;
    
    private String gender;
    
    @Column(nullable = false, unique = true)
    private String phone;
    
    private String email;
    private Integer age;
    
    @OneToMany(mappedBy = "user")
    private List<Appointment> appointments;
    
    @OneToMany(mappedBy = "user")
    private List<MedicalRecord> medicalRecords;
    
    @OneToMany(mappedBy = "user")
    private List<Billing> billings;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public List<Appointment> getAppointments() {
        return appointments;
    }
    
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }
    
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }
    
    public List<Billing> getBillings() {
        return billings;
    }
    
    public void setBillings(List<Billing> billings) {
        this.billings = billings;
    }
}