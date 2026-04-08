package com.hospital.mapper;

import com.hospital.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RegistrationMapper {
    int insert(Registration registration);
    int update(Registration registration);
    int deleteById(Long id);
    Registration selectById(Long id);
    List<Registration> selectAll();
    List<Registration> findByPatientId(@Param("patientId") Long patientId);
    List<Registration> findByDoctorId(@Param("doctorId") Long doctorId);
}