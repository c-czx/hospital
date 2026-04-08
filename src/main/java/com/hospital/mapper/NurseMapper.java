package com.hospital.mapper;

import com.hospital.entity.Nurse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NurseMapper {
    int insert(Nurse nurse);
    int update(Nurse nurse);
    int deleteById(Long id);
    Nurse selectById(Long id);
    List<Nurse> selectAll();
    List<Nurse> findByUserId(@Param("userId") Long userId);
}