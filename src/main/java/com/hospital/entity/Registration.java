package com.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 挂号实体类
 * 用于表示患者的挂号记录，包括挂号科室、医生、时间、状态、费用等
 */
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关联患者
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // 关联科室
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // 关联医生 (如果已经指定了医生)
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // 挂号时间
    @Column(name = "reg_time", nullable = false)
    private LocalDateTime regTime;

    // 挂号状态 (非常重要，护士端会更新这个状态)
    // 0: 挂号成功(待缴费), 1: 已缴费(待叫号), 2: 诊疗中, 3: 已完成, 4: 已退号
    @Column(name = "status", length = 20)
    private String status;

    // 挂号费用
    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee;

    // 排队号 (如 A001)
    @Column(name = "queue_number", length = 20)
    private String queueNumber;

    // Constructors
    public Registration() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }
}