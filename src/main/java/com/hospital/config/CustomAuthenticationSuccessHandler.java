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
        System.out.println("【请求中的 message 参数】" + request.getParameter("message"));
        System.out.println("【请求中的 success 参数】" + request.getParameter("success"));
        System.out.println("========================================");
        
        // 检查选择的角色是否与实际角色匹配
        String selectedRole = request.getParameter("role");
        String actualRole = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        
        if (selectedRole != null && !selectedRole.equals(actualRole)) {
            System.out.println("【角色不匹配】选择的角色：" + selectedRole + "，实际角色：" + actualRole);
            response.sendRedirect("/login?error=true&role_mismatch=true");
            return;
        }
        
        // 检查是否从电话号码更新后登录
        String message = request.getParameter("message");
        String success = request.getParameter("success");
        
        if (message != null && message.equals("phone_updated") && success != null && success.equals("1")) {
            // 如果是从电话号码更新后登录，重定向到个人信息页面并显示成功提示
            System.out.println("【重定向】跳转到个人信息页面：/patient/profile?success=1");
            response.sendRedirect("/patient/profile?success=1");
            return;
        }
        
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
