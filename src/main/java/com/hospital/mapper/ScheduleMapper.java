package com.hospital.mapper;

import com.hospital.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    int insert(Schedule schedule);
    int update(Schedule schedule);
    int deleteById(Long id);
    Schedule selectById(Long id);
    List<Schedule> selectAll();
    List<Schedule> findByDoctorId(@Param("doctorId") Long doctorId);
}