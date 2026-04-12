package com.hospital.controller;

import com.hospital.entity.Backup;
import com.hospital.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 管理员数据管理控制器
 * 提供数据备份、恢复等功能
 */
@Controller
@RequestMapping("/admin")
public class DataController {
    
    @Autowired
    private BackupService backupService;
    
    /**
     * 显示数据管理页面
     */
    @GetMapping("/data")
    public String data(Model model) {
        // 获取所有备份记录
        List<Backup> backups = backupService.getAllBackups();
        
        model.addAttribute("backups", backups);
        
        return "admin/data";
    }
    
    /**
     * 执行数据备份
     */
    @PostMapping("/data/backup")
    public String backup(@RequestParam String backupType, 
                        @RequestParam(required = false) String description,
                        RedirectAttributes redirectAttributes) {
        try {
            Backup backup = backupService.createBackup(backupType, description);
            if ("SUCCESS".equals(backup.getStatus())) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "备份成功！文件大小：" + backupService.formatFileSize(backup.getFileSize()));
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "备份失败：" + backup.getRemarks());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "备份异常：" + e.getMessage());
        }
        return "redirect:/admin/data";
    }
    
    /**
     * 恢复数据
     */
    @PostMapping("/data/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String result = backupService.restoreDatabase(id);
            if (result.contains("成功")) {
                redirectAttributes.addFlashAttribute("successMessage", result);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "恢复异常：" + e.getMessage());
        }
        return "redirect:/admin/data";
    }
    
    /**
     * 下载备份文件
     */
    @GetMapping("/data/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            Backup backup = backupService.getBackupById(id);
            Resource resource = new FileSystemResource(backup.getFilePath());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/sql")
                .header(HttpHeaders.CONTENT_LENGTH, 
                    String.valueOf(resource.contentLength()))
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除备份
     */
    @PostMapping("/data/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            backupService.deleteBackup(id);
            redirectAttributes.addFlashAttribute("successMessage", "删除备份成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "删除失败：" + e.getMessage());
        }
        return "redirect:/admin/data";
    }
}
