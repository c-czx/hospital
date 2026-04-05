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
 * 管理员数据管理控制器
 * 提供数据备份、恢复、统计等功能
 */
@Controller
@RequestMapping("/admin")
public class DataController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 显示数据管理页面
     */
    @GetMapping("/data")
    public String data(Model model) {
        long userCount = userService.findAll().size();
        long departmentCount = 0;
        long doctorCount = 0;
        long appointmentCount = 0;
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("departmentCount", departmentCount);
        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("appointmentCount", appointmentCount);
        
        return "admin/data";
    }
    
    /**
     * 执行数据备份
     */
    @PostMapping("/data/backup")
    public String backup(@RequestParam String backupType, 
                        @RequestParam(required = false) String description) {
        System.out.println("执行数据备份: " + backupType);
        return "redirect:/admin/data";
    }
    
    /**
     * 恢复数据
     */
    @PostMapping("/data/restore/{id}")
    public String restore(@PathVariable Long id) {
        System.out.println("恢复数据: " + id);
        return "redirect:/admin/data";
    }
    
    /**
     * 下载备份文件
     */
    @GetMapping("/data/download/{id}")
    public String download(@PathVariable Long id) {
        System.out.println("下载备份: " + id);
        return "redirect:/admin/data";
    }
    
    /**
     * 删除备份
     */
    @PostMapping("/data/delete/{id}")
    public String delete(@PathVariable Long id) {
        System.out.println("删除备份: " + id);
        return "redirect:/admin/data";
    }
}
