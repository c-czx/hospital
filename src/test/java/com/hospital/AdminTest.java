/**
*本代码由Muyvge编写，仅用于学习交流，不得用于商业用途
*本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
*/

package com.hospital;

import com.hospital.entity.User;
import com.hospital.entity.Doctor;
import com.hospital.repository.UserRepository;
import com.hospital.service.UserService;
import com.hospital.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员功能测试类
 * 测试管理员相关的用户管理、权限管理等功能
 */
@SpringBootTest
public class AdminTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;
    
    /**
     * 测试管理员账户初始化
     */
    @Test
    public void testAdminAccount() {
        User admin = userRepository.findByPhone("13800000000").orElse(null);
        assertNotNull(admin, "管理员账户应该存在");
        assertEquals("13800000000", admin.getPhone());
        assertEquals("ADMIN", admin.getRole());
        assertTrue(admin.getPassword().startsWith("$2a$"), "密码应该使用BCrypt加密");
    }
    
    /**
     * 测试用户创建
     */
    @Test
    public void testCreateUser() {
        User user = new User();
        user.setPassword("test123");
        user.setName("测试用户");
        user.setRole("PATIENT");
        user.setGender("male");
        user.setPhone("13900000000");
        user.setEmail("test@example.com");
        
        User savedUser = userService.saveUser(user);
        assertNotNull(savedUser.getId(), "用户应该有ID");
        assertEquals("13900000000", savedUser.getPhone());
        
        // 清理
        userRepository.deleteById(savedUser.getId());
    }
    
    /**
     * 测试用户查找
     */
    @Test
    public void testFindUser() {
        User user = userRepository.findByPhone("13800000000").orElse(null);
        assertNotNull(user);
        assertEquals("系统管理员", user.getName());
        assertEquals("ADMIN", user.getRole());
    }
    
    /**
     * 测试用户列表
     */
    @Test
    public void testFindAllUsers() {
        List<User> users = userRepository.findAll();
        assertFalse(users.isEmpty(), "用户列表不应该为空");
        
        boolean hasAdmin = users.stream()
            .anyMatch(u -> "13800000000".equals(u.getPhone()) && "ADMIN".equals(u.getRole()));
        assertTrue(hasAdmin, "应该包含管理员账户");
    }
    
    /**
     * 测试用户更新
     */
    @Test
    public void testUpdateUser() {
        User admin = userRepository.findByPhone("13800000000").orElse(null);
        assertNotNull(admin);
        
        String originalName = admin.getName();
        admin.setName("更新后的管理员");
        userService.updateUser(admin);
        
        User updatedUser = userRepository.findByPhone("13800000000").orElse(null);
        assertEquals("更新后的管理员", updatedUser.getName());
        
        // 恢复
        updatedUser.setName(originalName);
        userService.updateUser(updatedUser);
    }
    
    /**
     * 测试用户删除
     */
    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setPassword("temp123");
        user.setName("临时用户");
        user.setRole("PATIENT");
        user.setPhone("13900000001");
        
        User savedUser = userService.saveUser(user);
        assertNotNull(savedUser.getId());
        
        userService.deleteUser(savedUser.getId());
        assertNull(userRepository.findById(savedUser.getId()).orElse(null));
    }
    
    /**
     * 测试手机号唯一性
     */
    @Test
    public void testUniquePhone() {
        boolean exists = userRepository.existsByPhone("13800000000");
        assertTrue(exists, "admin 手机号应该存在");
    }
    
    /**
     * 测试医生角色用户创建
     */
    @Test
    public void testCreateDoctorUser() {
        User doctor = new User();
        doctor.setPassword("doctor123");
        doctor.setName("测试医生");
        doctor.setRole("DOCTOR");
        doctor.setGender("male");
        doctor.setPhone("13900000002");
        
        User savedDoctor = userService.saveUser(doctor);
        assertNotNull(savedDoctor.getId());
        
        // 验证医生记录是否自动创建
        Doctor savedDoctorRecord = doctorService.findByUserId(savedDoctor.getId());
        assertNotNull(savedDoctorRecord, "医生记录应该自动创建");
        assertEquals(savedDoctor.getId(), savedDoctorRecord.getUser().getId());
        
        // 先删除医生记录，再删除用户（避免外键约束错误）
        doctorService.deleteDoctor(savedDoctorRecord.getId());
        userService.deleteUser(savedDoctor.getId());
    }
}
