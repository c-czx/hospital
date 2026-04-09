package com.hospital.repository;

import com.hospital.entity.Checkup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckupRepository extends JpaRepository<Checkup, Long> {
    List<Checkup> findByDoctor_Id(Long doctorId);
    List<Checkup> findByUser_Id(Long userId);
}