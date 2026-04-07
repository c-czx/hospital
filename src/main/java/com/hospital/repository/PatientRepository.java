package com.hospital.repository;

import com.hospital.entity.Patient;
import com.hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // 根据关联的 User 对象查找 Patient
    Patient findByUser(User user);
}