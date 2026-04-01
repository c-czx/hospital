package com.hospital.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                    Authentication authentication) throws IOException {
        // 【调试信息】认证成功
        System.out.println("========================================");
        System.out.println("【认证成功】用户：" + authentication.getName());
        System.out.println("【角色信息】" + authentication.getAuthorities().iterator().next().getAuthority());
        System.out.println("【请求中的 role 参数】" + request.getParameter("role"));
        System.out.println("========================================");
        
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
        
        switch (userRole) {
            case "ROLE_PATIENT":
                System.out.println("【重定向】跳转到患者端：/patient/dashboard");
                response.sendRedirect("/patient/dashboard");
                break;
            case "ROLE_DOCTOR":
                System.out.println("【重定向】跳转到医生端：/doctor/dashboard");
                response.sendRedirect("/doctor/dashboard");
                break;
            case "ROLE_NURSE":
                System.out.println("【重定向】跳转到护士端：/nurse/dashboard");
                response.sendRedirect("/nurse/dashboard");
                break;
            case "ROLE_ADMIN":
                System.out.println("【重定向】跳转到管理员端：/admin/dashboard");
                response.sendRedirect("/admin/dashboard");
                break;
            default:
                System.out.println("【重定向】跳转到默认页面：/dashboard");
                response.sendRedirect("/dashboard");
                break;
        }
    }
}
