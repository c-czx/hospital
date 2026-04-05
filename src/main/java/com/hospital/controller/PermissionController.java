/**
*本代码由Muyvge编写，仅用于学习交流，不得用于商业用途
*本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
*/

package com.hospital.controller;

import com.hospital.entity.User;
import com.hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 管理员权限管理控制器
 * 提供用户权限管理功能
 */
@Controller
@RequestMapping("/admin")
public class PermissionController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 显示权限管理页面
     */
    @GetMapping("/permissions")
    public String permissions(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/permissions";
    }
    
    /**
     * 更新用户角色权限
     */
    @PostMapping("/permission/update/{id}")
    public String updatePermission(@PathVariable Long id, @RequestParam String role) {
        User user = userService.findById(id);
        if (user != null) {
            user.setRole(role);
            userService.updateUser(user);
        }
        return "redirect:/admin/permissions";
    }
}
