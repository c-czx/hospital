package com.hospital.mapper;

import com.hospital.entity.Billing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BillingMapper {
    int insert(Billing billing);
    int update(Billing billing);
    int deleteById(Long id);
    Billing selectById(Long id);
    List<Billing> selectAll();
    List<Billing> findByPatientId(@Param("patientId") Long patientId);
    List<Billing> findByStatus(@Param("status") String status);
}