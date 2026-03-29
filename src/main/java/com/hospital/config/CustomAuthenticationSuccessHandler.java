package com.hospital.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                    Authentication authentication) throws IOException {
        String role = request.getParameter("role");
        String userRole = authentication.getAuthorities().iterator().next().getAuthority();
        
        if (role != null && userRole.equals("ROLE_" + role)) {
            switch (role) {
                case "PATIENT":
                    response.sendRedirect("/patient/dashboard");
                    break;
                case "DOCTOR":
                    response.sendRedirect("/doctor/dashboard");
                    break;
                case "NURSE":
                    response.sendRedirect("/nurse/dashboard");
                    break;
                case "ADMIN":
                    response.sendRedirect("/admin/dashboard");
                    break;
                default:
                    response.sendRedirect("/dashboard");
                    break;
            }
        } else {
            response.sendRedirect("/dashboard");
        }
    }
}
