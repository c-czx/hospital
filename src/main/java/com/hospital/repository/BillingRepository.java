package com.hospital.repository;

import com.hospital.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findByUserId(Long userId);
    List<Billing> findByStatus(String status);
}