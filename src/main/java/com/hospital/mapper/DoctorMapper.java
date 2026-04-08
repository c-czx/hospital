package com.hospital.mapper;

import com.hospital.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DoctorMapper {
    int insert(Doctor doctor);
    int update(Doctor doctor);
    int deleteById(Long id);
    Doctor selectById(Long id);
    List<Doctor> selectAll();
    List<Doctor> findByDepartmentId(@Param("departmentId") Long departmentId);
    List<Doctor> findByUserId(@Param("userId") Long userId);
}