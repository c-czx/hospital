/**
*本代码由Muyvge编写，仅用于学习交流，不得用于商业用途
*本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
*/

package com.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理员系统设置控制器
 * 提供系统参数配置功能
 */
@Controller
@RequestMapping("/admin")
public class SettingsController {
    
    /**
     * 显示系统设置页面
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("hospitalName", "医院挂号系统");
        model.addAttribute("hospitalAddress", "北京市朝阳区");
        model.addAttribute("hospitalPhone", "010-12345678");
        model.addAttribute("hospitalDescription", "提供便捷的挂号服务");
        model.addAttribute("maxAppointmentsPerDay", 100);
        model.addAttribute("advanceDays", 7);
        model.addAttribute("scheduleReleaseTime", "08:00");
        model.addAttribute("maintenanceMode", false);
        model.addAttribute("maintenanceMessage", "系统维护中，请稍后再试");
        model.addAttribute("logRetentionDays", 30);
        
        return "admin/settings";
    }
    
    /**
     * 保存医院信息设置
     */
    @PostMapping("/settings/hospital")
    public String saveHospital(@RequestParam String hospitalName,
                              @RequestParam String hospitalAddress,
                              @RequestParam String hospitalPhone,
                              @RequestParam String hospitalDescription) {
        System.out.println("保存医院信息: " + hospitalName);
        return "redirect:/admin/settings";
    }
    
    /**
     * 保存预约设置
     */
    @PostMapping("/settings/appointment")
    public String saveAppointment(@RequestParam int maxAppointmentsPerDay,
                                 @RequestParam int advanceDays,
                                 @RequestParam String scheduleReleaseTime) {
        System.out.println("保存预约设置: " + maxAppointmentsPerDay);
        return "redirect:/admin/settings";
    }
    
    /**
     * 保存系统配置
     */
    @PostMapping("/settings/system")
    public String saveSystem(@RequestParam boolean maintenanceMode,
                            @RequestParam String maintenanceMessage,
                            @RequestParam int logRetentionDays) {
        System.out.println("保存系统配置: " + maintenanceMode);
        return "redirect:/admin/settings";
    }
}
