package com.hospital.mapper;

import com.hospital.entity.Advice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AdviceMapper {
    int insert(Advice advice);
    int update(Advice advice);
    int deleteById(Long id);
    Advice selectById(Long id);
    List<Advice> selectAll();
    List<Advice> findByUserId(@Param("userId") Long userId);
    List<Advice> findByDoctorId(@Param("doctorId") Long doctorId);
}