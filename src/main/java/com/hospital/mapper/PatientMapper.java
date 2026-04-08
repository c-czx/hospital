package com.hospital.mapper;

import com.hospital.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PatientMapper {
    int insert(Patient patient);
    int update(Patient patient);
    int deleteById(Long id);
    Patient selectById(Long id);
    List<Patient> selectAll();
    List<Patient> findByUserId(@Param("userId") Long userId);
}