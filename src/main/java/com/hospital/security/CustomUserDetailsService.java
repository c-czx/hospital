package com.hospital.security;

import com.hospital.entity.User;
import com.hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务
 * 用户登录后，Spring Security使用该类根据用户输入的手机号查询，获取用户详情
 * 根据用户详情验证密码是否匹配
 * 用于Spring Security认证
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        System.out.println("========================================");
        System.out.println("【尝试登录】手机号：" + phone);
        
        User user = userService.findByPhone(phone);
        
        if (user == null) {
            System.out.println("【用户不存在】手机号：" + phone);
            System.out.println("========================================");
            throw new UsernameNotFoundException("用户不存在");
        }
        
        System.out.println("【用户找到】姓名：" + user.getName());
        System.out.println("【用户角色】" + user.getRole());
        System.out.println("【加密密码】" + user.getPassword().substring(0, 10) + "...");
        System.out.println("========================================");
        
        return new org.springframework.security.core.userdetails.User(
            user.getPhone(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
