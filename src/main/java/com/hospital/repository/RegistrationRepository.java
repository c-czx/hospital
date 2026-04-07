package com.hospital.repository;

import com.hospital.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // 1. 查询某个患者的所有挂号记录
    @Query("SELECT r FROM Registration r WHERE r.patient.patientId = :patientId")
    List<Registration> findByPatientId(@Param("patientId") Long patientId);

    // 2. 查询某个科室当天的挂号列表（护士分诊台需要看这个）
    // 假设表里有 department_id 字段
    List<Registration> findByDepartmentIdAndRegTimeBetween(Long deptId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    // 3. 根据状态查询 (例如：查找所有 "待缴费" 的记录)
    List<Registration> findByStatus(String status);
}