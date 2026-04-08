package com.hospital.mapper;

import com.hospital.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AppointmentMapper {
    int insert(Appointment appointment);
    int update(Appointment appointment);
    int deleteById(Long id);
    Appointment selectById(Long id);
    List<Appointment> selectAll();
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);
    List<Appointment> findByStatus(@Param("status") String status);
}