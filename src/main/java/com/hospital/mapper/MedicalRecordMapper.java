package com.hospital.mapper;

import com.hospital.entity.MedicalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MedicalRecordMapper {
    int insert(MedicalRecord medicalRecord);
    int update(MedicalRecord medicalRecord);
    int deleteById(Long id);
    MedicalRecord selectById(Long id);
    List<MedicalRecord> selectAll();
    List<MedicalRecord> findByPatientId(@Param("patientId") Long patientId);
    List<MedicalRecord> findByDoctorId(@Param("doctorId") Long doctorId);
}