/**
 * 本代码由 Muyvge 编写，仅用于学习交流，不得用于商业用途
 * 本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
 */
package com.hospital.service;

import com.hospital.entity.Backup;
import com.hospital.repository.BackupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 数据库备份与恢复服务
 * 提供数据库的备份、恢复、下载等功能
 */
@Service
public class BackupService {

    @Autowired
    private BackupRepository backupRepository;

    /**
     * 数据库连接 URL
     */
    @Value("${spring.datasource.url}")
    private String databaseUrl;

    /**
     * 数据库用户名
     */
    @Value("${spring.datasource.username}")
    private String databaseUsername;

    /**
     * 数据库密码
     */
    @Value("${spring.datasource.password}")
    private String databasePassword;

    /**
     * 备份文件存储目录
     */
    private static final String BACKUP_DIRECTORY = "backups";

    /**
     * mysqldump 命令路径（Windows 系统）
     * 如果 MySQL 添加到环境变量，可直接使用 "mysqldump"
     */
    private static final String MYSQL_DUMP_COMMAND = "mysqldump";

    /**
     * mysql 命令路径（Windows 系统）
     */
    private static final String MYSQL_COMMAND = "mysql";

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 初始化服务，创建备份目录
     */
    @PostConstruct
    public void init() {
        // 从数据库 URL 中提取数据库名称
        // 格式：jdbc:mysql://localhost:3306/hospital_db?...
        if (databaseUrl.contains("/")) {
            String dbPart = databaseUrl.substring(databaseUrl.lastIndexOf("/") + 1);
            databaseName = dbPart.split("\\?")[0];
        }

        // 创建备份目录
        File backupDir = new File(BACKUP_DIRECTORY);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
            System.out.println("创建备份目录：" + BACKUP_DIRECTORY);
        }
    }

    /**
     * 执行数据库备份
     * @param backupType 备份类型：FULL 或 INCREMENTAL
     * @param description 备份说明
     * @return 备份记录
     */
    @Transactional
    public Backup createBackup(String backupType, String description) {
        System.out.println("开始备份数据库：" + databaseName + "，类型：" + backupType);

        Backup backup = new Backup();
        backup.setBackupType(backupType);
        backup.setDescription(description);
        backup.setBackupTime(LocalDateTime.now());

        try {
            // 生成备份文件名
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            );
            String fileName = "backup_" + backupType.toLowerCase() + "_" + timestamp + ".sql";
            String filePath = BACKUP_DIRECTORY + File.separator + fileName;

            // 构建 mysqldump 命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                MYSQL_DUMP_COMMAND,
                "-h", "localhost",
                "-u", databaseUsername,
                "-p" + databasePassword,
                "--default-character-set=utf8mb4",
                "--single-transaction",
                "--quick",
                databaseName
            );

            // 执行命令
            Process process = processBuilder.start();
            
            // 使用独立线程读取错误流（警告信息），避免阻塞
            final Process finalProcess = process;
            Thread errorReaderThread = new Thread(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(finalProcess.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("[mysqldump 警告] " + line);
                    }
                } catch (IOException e) {
                    // 忽略错误流读取异常
                }
            });
            errorReaderThread.setDaemon(true);
            errorReaderThread.start();
            
            // 读取标准输出（SQL 内容）
            java.io.InputStream inputStream = process.getInputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            // 等待完成（设置超时）
            boolean completed = process.waitFor(300, java.util.concurrent.TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                backup.setFilePath("");
                backup.setFileSize(0L);
                backup.setStatus("FAILED");
                backup.setRemarks("备份超时，已强制终止进程");
                return backupRepository.save(backup);
            }
            
            // 检查执行结果
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                // 备份成功，写入文件
                byte[] backupData = outputStream.toByteArray();
                Files.write(Paths.get(filePath), backupData);
                
                // 计算文件大小
                long fileSize = new File(filePath).length();
                
                // 更新备份记录
                backup.setFilePath(filePath);
                backup.setFileSize(fileSize);
                backup.setStatus("SUCCESS");
                backup.setRemarks("备份成功");
                
                System.out.println("备份成功：" + filePath + "，大小：" + fileSize + " 字节");
            } else {
                // 备份失败
                backup.setFilePath("");
                backup.setFileSize(0L);
                backup.setStatus("FAILED");
                
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getErrorStream())
                );
                StringBuilder errorBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorBuilder.append(line).append("\n");
                }
                backup.setRemarks("备份失败：" + errorBuilder.toString());
                
                System.out.println("备份失败，退出码：" + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            backup.setFilePath("");
            backup.setFileSize(0L);
            backup.setStatus("FAILED");
            backup.setRemarks("备份异常：" + e.getMessage());
            
            System.out.println("备份异常：" + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // 保存备份记录到数据库
        return backupRepository.save(backup);
    }

    /**
     * 恢复数据库
     * @param backupId 备份记录 ID
     * @return 恢复结果信息
     */
    @Transactional
    public String restoreDatabase(Long backupId) {
        System.out.println("开始恢复数据库，备份 ID：" + backupId);

        // 查询备份记录
        Backup backup = backupRepository.findById(backupId)
            .orElseThrow(() -> new RuntimeException("备份记录不存在：" + backupId));

        // 检查备份文件是否存在
        File backupFile = new File(backup.getFilePath());
        if (!backupFile.exists()) {
            return "备份文件不存在：" + backup.getFilePath();
        }

        Process process = null;
        Thread outputReaderThread = null;
        try {
            System.out.println("[恢复] 开始构建 MySQL 命令...");
            
            // 方法 4：使用 ProcessBuilder 直接执行 mysql 命令，通过输入流写入数据
            // 测试发现 PowerShell 管道会导致字符编码问题，所以改用 Java 原生方式
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(
                MYSQL_COMMAND,
                "-h", "localhost",
                "-u", databaseUsername,
                "-p" + databasePassword,
                "--default-character-set=utf8mb4",
                databaseName
            );
            processBuilder.redirectErrorStream(true);
            
            System.out.println("[恢复] 命令：" + String.join(" ", processBuilder.command()));
            System.out.println("[恢复] 备份文件：" + backupFile.getAbsolutePath());
            System.out.println("[恢复] 文件大小：" + backupFile.length() + " 字节");
            
            // 执行命令
            System.out.println("[恢复] 启动 MySQL 进程...");
            process = processBuilder.start();
            System.out.println("[恢复] MySQL 进程已启动，PID: " + process.pid());
            
            // 使用独立线程读取输出
            final Process finalProcess = process;
            outputReaderThread = new Thread(() -> {
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(finalProcess.getInputStream(), "UTF-8"))) {
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[MySQL] " + line);
                        lineCount++;
                    }
                    System.out.println("[恢复] 读取输出完成，共 " + lineCount + " 行");
                } catch (IOException e) {
                    System.err.println("[恢复] 读取输出异常：" + e.getMessage());
                    e.printStackTrace();
                }
            });
            outputReaderThread.setName("output-reader");
            outputReaderThread.start();
            System.out.println("[恢复] 输出读取线程已启动");
            
            // 写入备份文件内容到 mysql 进程
            System.out.println("[恢复] 开始写入备份文件内容...");
            long startTime = System.currentTimeMillis();
            try (java.io.InputStream inputStream = new java.io.FileInputStream(backupFile);
                 java.io.OutputStream outputStream = process.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;
                int writeCount = 0;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush(); // 每次都 flush，避免缓冲
                    totalBytes += bytesRead;
                    writeCount++;
                    
                    // 每写入 80KB 打印一次进度
                    if (writeCount % 10 == 0) {
                        System.out.println("[恢复] 写入进度：" + (totalBytes / 1024) + " KB (" + writeCount + " 次)");
                    }
                }
                
                long endTime = System.currentTimeMillis();
                System.out.println("[恢复] 写入完成！共写入 " + (totalBytes / 1024) + " KB，耗时 " + (endTime - startTime) + "ms");
            } catch (IOException e) {
                System.err.println("[恢复] 写入备份文件时异常：" + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            
            // 等待一小段时间，确保输出流完全关闭
            System.out.println("[恢复] 等待输出流关闭...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[恢复] 输出流已关闭");
            
            System.out.println("[恢复] 等待 MySQL 进程执行完成（最多 300 秒）...");
            
            // 使用带超时的 waitFor
            boolean processCompleted = process.waitFor(300, java.util.concurrent.TimeUnit.SECONDS);
            
            if (!processCompleted) {
                System.out.println("[恢复] 进程执行超时！强制终止...");
                process.destroyForcibly();
            } else {
                System.out.println("[恢复] MySQL 进程已退出，退出码：" + process.exitValue());
            }
            
            // 等待读取线程完成（最多 10 秒）
            System.out.println("[恢复] 等待输出读取线程完成（最多 10 秒）...");
            try {
                if (outputReaderThread != null && outputReaderThread.isAlive()) {
                    outputReaderThread.join(10000);
                    if (outputReaderThread.isAlive()) {
                        System.out.println("[恢复] 警告：输出读取线程超时，强制中断");
                    } else {
                        System.out.println("[恢复] 输出读取线程已完成");
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("[恢复] 等待线程时被中断");
                Thread.currentThread().interrupt();
            }
            
            System.out.println("[恢复] 检查最终状态...");
            // 检查执行结果
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                System.out.println("数据库恢复成功");
                return "数据库恢复成功";
            } else {
                System.out.println("数据库恢复失败，退出码：" + exitCode);
                return "数据库恢复失败，退出码：" + exitCode;
            }

        } catch (IOException | InterruptedException e) {
            if (process != null) {
                process.destroyForcibly();
            }
            System.out.println("数据库恢复异常：" + e.getMessage());
            Thread.currentThread().interrupt();
            return "数据库恢复异常：" + e.getMessage();
        }
    }

    /**
     * 获取所有备份记录
     * @return 备份记录列表
     */
    public List<Backup> getAllBackups() {
        return backupRepository.findAllByOrderByBackupTimeDesc();
    }

    /**
     * 根据 ID 获取备份记录
     * @param id 备份 ID
     * @return 备份记录
     */
    public Backup getBackupById(Long id) {
        return backupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("备份记录不存在：" + id));
    }

    /**
     * 删除备份记录及文件
     * @param id 备份 ID
     */
    @Transactional
    public void deleteBackup(Long id) {
        Backup backup = backupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("备份记录不存在：" + id));

        // 删除备份文件
        File backupFile = new File(backup.getFilePath());
        if (backupFile.exists()) {
            if (backupFile.delete()) {
                System.out.println("删除备份文件：" + backup.getFilePath());
            } else {
                System.out.println("删除备份文件失败：" + backup.getFilePath());
            }
        }

        // 删除备份记录
        backupRepository.delete(backup);
        System.out.println("删除备份记录：" + id);
    }

    /**
     * 获取备份文件
     * @param id 备份 ID
     * @return 备份文件
     */
    public File getBackupFile(Long id) {
        Backup backup = getBackupById(id);
        File backupFile = new File(backup.getFilePath());
        
        if (!backupFile.exists()) {
            throw new RuntimeException("备份文件不存在：" + backup.getFilePath());
        }
        
        return backupFile;
    }

    /**
     * 格式化文件大小
     * @param bytes 字节数
     * @return 格式化后的大小
     */
    public String formatFileSize(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
}
