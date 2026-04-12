/**
 * 本代码由 Muyvge 编写，仅用于学习交流，不得用于商业用途
 * 本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
 */
package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据库备份实体类
 * 用于记录每次备份的信息
 */
@Entity
@Table(name = "backups")
public class Backup {

    /**
     * 备份记录 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 备份类型：FULL-完整备份，INCREMENTAL-增量备份
     */
    @Column(nullable = false, length = 20)
    private String backupType;

    /**
     * 备份文件路径
     */
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * 备份文件大小（字节）
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * 备份说明
     */
    @Column(length = 500)
    private String description;

    /**
     * 备份时间
     */
    @Column(nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime backupTime;

    /**
     * 备份状态：SUCCESS-成功，FAILED-失败
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 备注信息（如失败原因等）
     */
    @Column(length = 1000)
    private String remarks;

    /**
     * 默认构造函数
     */
    public Backup() {
    }

    /**
     * 带参数构造函数
     */
    public Backup(String backupType, String filePath, Long fileSize, 
                  String description, String status) {
        this.backupType = backupType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.description = description;
        this.status = status;
        this.backupTime = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackupType() {
        return backupType;
    }

    public void setBackupType(String backupType) {
        this.backupType = backupType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(LocalDateTime backupTime) {
        this.backupTime = backupTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Backup{" +
                "id=" + id +
                ", backupType='" + backupType + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", description='" + description + '\'' +
                ", backupTime=" + backupTime +
                ", status='" + status + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
