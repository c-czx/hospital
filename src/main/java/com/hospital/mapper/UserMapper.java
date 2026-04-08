package com.hospital.mapper;

import com.hospital.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);
    int update(User user);
    int deleteById(Long id);
    User selectById(Long id);
    List<User> selectAll();
    User findByPhone(@Param("phone") String phone);
    List<User> findByRole(@Param("role") String role);
    boolean existsByPhone(@Param("phone") String phone);
}