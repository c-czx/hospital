package com.hospital.mapper;

import com.hospital.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    int insert(Department department);
    int update(Department department);
    int deleteById(Long id);
    Department selectById(Long id);
    List<Department> selectAll();
}