package com.hospital.controller;

import com.hospital.entity.User;
import com.hospital.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String gender, 
                          @RequestParam String phone, @RequestParam String email,
                          @RequestParam Integer age, @RequestParam String password, 
                          @RequestParam String captcha, @RequestParam String role,
                          HttpSession session, Model model) {
        
        if (!CaptchaController.validateCaptcha(session, captcha)) {
            model.addAttribute("error", "验证码错误");
            return "register";
        }
        
        if (userService.existsByPhone(phone)) {
            model.addAttribute("error", "该手机号码已注册");
            return "register";
        }
        
        User user = new User();
        user.setPassword(password);
        user.setName(name);
        user.setRole(role);
        user.setGender(gender);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAge(age);
        
        userService.saveUser(user);
        
        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.equals("ROLE_PATIENT")) {
                return "redirect:/patient/dashboard";
            } else if (role.equals("ROLE_DOCTOR")) {
                return "redirect:/doctor/dashboard";
            } else if (role.equals("ROLE_NURSE")) {
                return "redirect:/nurse/dashboard";
            } else if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }
        return "redirect:/login";
    }
}