package com.hospital.repository;

import com.hospital.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NurseRepository extends JpaRepository<Nurse, Long> {
    // 根据用户 ID 查找护士信息
    Optional<Nurse> findByUserId(Long userId);
}