package com.hospital.mapper;

import com.hospital.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PrescriptionMapper {
    int insert(Prescription prescription);
    int update(Prescription prescription);
    int deleteById(Long id);
    Prescription selectById(Long id);
    List<Prescription> selectAll();
    List<Prescription> findByPatientId(@Param("patientId") Long patientId);
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);
}