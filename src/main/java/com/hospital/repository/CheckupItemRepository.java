package com.hospital.repository;

import com.hospital.entity.CheckupItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckupItemRepository extends JpaRepository<CheckupItem, Long> {
}
