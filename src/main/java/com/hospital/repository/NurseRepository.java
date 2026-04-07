package com.hospital.repository;

import com.hospital.entity.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NurseRepository extends JpaRepository<Nurse, Integer> {
    // 根据用户ID查找护士信息
    Optional<Nurse> findByUserId(Integer userId);
}