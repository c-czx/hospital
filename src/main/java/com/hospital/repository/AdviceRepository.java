package com.hospital.repository;

import com.hospital.entity.Advice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdviceRepository extends JpaRepository<Advice, Long> {
    List<Advice> findByUserId(Long userId);
    List<Advice> findByDoctorId(Long doctorId);
}
