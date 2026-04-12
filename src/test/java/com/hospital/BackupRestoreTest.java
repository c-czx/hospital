/**
*本代码由 Muyvge 编写，仅用于学习交流，不得用于商业用途
*本代码的使用、复制、修改、合并、发布、分发、再许可、销售等行为均受到法律的严格限制。
*/

package com.hospital;

import com.hospital.entity.Backup;
import com.hospital.repository.BackupRepository;
import com.hospital.service.BackupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 备份恢复功能测试类
 * 用于测试数据库备份和恢复功能，排查恢复过程中的问题
 */
@SpringBootTest
public class BackupRestoreTest {
    
    @Autowired
    private BackupService backupService;
    
    @Autowired
    private BackupRepository backupRepository;
    
    /**
     * 测试 1：直接执行 PowerShell 命令进行恢复
     * 目的：验证 PowerShell 命令本身是否能正常工作
     */
    @Test
    public void testPowerShellCommand() throws IOException, InterruptedException {
        System.out.println("========== 测试 1：PowerShell 命令执行 ==========");
        
        // 查找最新的备份文件
        File backupDir = new File("backups");
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (backupFiles == null || backupFiles.length == 0) {
            System.out.println("未找到备份文件，跳过测试");
            return;
        }
        
        // 使用最新的备份文件
        File latestBackup = backupFiles[backupFiles.length - 1];
        System.out.println("使用备份文件：" + latestBackup.getAbsolutePath());
        System.out.println("文件大小：" + latestBackup.length() + " 字节");
        
        // 构建 PowerShell 命令
        String command = String.format(
            "Get-Content '%s' | mysql -h localhost -u root -p123456 --default-character-set=utf8mb4 hospital_db",
            latestBackup.getAbsolutePath()
        );
        
        System.out.println("执行命令：" + command);
        
        // 执行命令
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        // 读取输出
        Thread outputThread = new Thread(() -> {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream(), "UTF-8"))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[STDOUT] " + line);
                    lineCount++;
                }
                System.out.println("输出行数：" + lineCount);
            } catch (IOException e) {
                System.err.println("读取输出异常：" + e.getMessage());
                e.printStackTrace();
            }
        });
        outputThread.start();
        
        // 等待完成（最多 60 秒）
        boolean completed = process.waitFor(60, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("进程完成状态：" + completed);
        
        if (!completed) {
            System.out.println("进程超时，强制终止");
            process.destroyForcibly();
        }
        
        // 等待输出线程完成
        outputThread.join(5000);
        
        // 检查退出码
        int exitCode = process.exitValue();
        System.out.println("退出码：" + exitCode);
        
        assertEquals(0, exitCode, "PowerShell 命令应该成功执行");
        System.out.println("========== 测试 1 完成 ==========\n");
    }
    
    /**
     * 测试 2：使用 ProcessBuilder 直接执行 mysql 命令（不使用 PowerShell 管道）
     * 目的：验证 Java 进程管理是否有问题
     */
    @Test
    public void testProcessBuilderDirect() throws IOException, InterruptedException {
        System.out.println("========== 测试 2：ProcessBuilder 直接执行 ==========");
        
        // 查找最新的备份文件
        File backupDir = new File("backups");
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (backupFiles == null || backupFiles.length == 0) {
            System.out.println("未找到备份文件，跳过测试");
            return;
        }
        
        File latestBackup = backupFiles[backupFiles.length - 1];
        System.out.println("使用备份文件：" + latestBackup.getAbsolutePath());
        
        // 使用 ProcessBuilder 直接执行 mysql 命令，通过输入流写入数据
        ProcessBuilder processBuilder = new ProcessBuilder(
            "mysql", "-h", "localhost", "-u", "root", "-p123456",
            "--default-character-set=utf8mb4", "hospital_db"
        );
        processBuilder.redirectErrorStream(true);
        
        System.out.println("启动 MySQL 进程...");
        Process process = processBuilder.start();
        
        // 读取输出
        Thread outputThread = new Thread(() -> {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream(), "UTF-8"))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[STDOUT] " + line);
                    lineCount++;
                }
                System.out.println("输出行数：" + lineCount);
            } catch (IOException e) {
                System.err.println("读取输出异常：" + e.getMessage());
                e.printStackTrace();
            }
        });
        outputThread.start();
        
        // 写入备份文件内容
        System.out.println("开始写入备份文件内容...");
        try (java.io.InputStream inputStream = Files.newInputStream(latestBackup.toPath());
             java.io.OutputStream outputStream = process.getOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;
            int writeCount = 0;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
                totalBytes += bytesRead;
                writeCount++;
                
                if (writeCount % 10 == 0) {
                    System.out.println("写入进度：" + (totalBytes / 1024) + " KB (" + writeCount + " 次)");
                }
            }
            
            System.out.println("写入完成，共写入 " + (totalBytes / 1024) + " KB");
        }
        
        // 等待完成
        boolean completed = process.waitFor(60, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("进程完成状态：" + completed);
        
        if (!completed) {
            System.out.println("进程超时，强制终止");
            process.destroyForcibly();
        }
        
        outputThread.join(5000);
        
        int exitCode = process.exitValue();
        System.out.println("退出码：" + exitCode);
        
        assertEquals(0, exitCode, "MySQL 命令应该成功执行");
        System.out.println("========== 测试 2 完成 ==========\n");
    }
    
    /**
     * 测试 3：使用 BackupService 进行恢复
     * 目的：测试完整的恢复流程
     */
    @Test
    public void testBackupServiceRestore() {
        System.out.println("========== 测试 3：BackupService 恢复 ==========");
        
        // 查找最新的备份记录
        java.util.List<Backup> backups = backupRepository.findAll();
        
        if (backups.isEmpty()) {
            System.out.println("未找到备份记录，跳过测试");
            return;
        }
        
        Backup latestBackup = backups.get(backups.size() - 1);
        System.out.println("使用备份记录 ID: " + latestBackup.getId());
        System.out.println("备份文件：" + latestBackup.getFilePath());
        
        // 执行恢复
        String result = backupService.restoreDatabase(latestBackup.getId());
        
        System.out.println("恢复结果：" + result);
        assertTrue(result.contains("成功"), "恢复应该成功：" + result);
        System.out.println("========== 测试 3 完成 ==========\n");
    }
    
    /**
     * 测试 4：检查备份文件内容
     * 目的：验证备份文件是否正常
     */
    @Test
    public void testBackupFileContent() throws IOException {
        System.out.println("========== 测试 4：备份文件内容检查 ==========");
        
        File backupDir = new File("backups");
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
        
        if (backupFiles == null || backupFiles.length == 0) {
            System.out.println("未找到备份文件，跳过测试");
            return;
        }
        
        File latestBackup = backupFiles[backupFiles.length - 1];
        System.out.println("检查文件：" + latestBackup.getAbsolutePath());
        System.out.println("文件大小：" + latestBackup.length() + " 字节");
        
        // 读取文件内容
        Path path = Paths.get(latestBackup.getAbsolutePath());
        java.util.List<String> lines = Files.readAllLines(path);
        
        System.out.println("文件行数：" + lines.size());
        System.out.println("前 10 行内容：");
        for (int i = 0; i < Math.min(10, lines.size()); i++) {
            System.out.println("  [" + i + "] " + lines.get(i));
        }
        
        // 检查是否包含有效的 SQL 语句
        boolean hasDropTable = lines.stream().anyMatch(line -> line.contains("DROP TABLE"));
        boolean hasCreateTable = lines.stream().anyMatch(line -> line.contains("CREATE TABLE"));
        boolean hasInsert = lines.stream().anyMatch(line -> line.contains("INSERT INTO"));
        
        System.out.println("包含 DROP TABLE: " + hasDropTable);
        System.out.println("包含 CREATE TABLE: " + hasCreateTable);
        System.out.println("包含 INSERT: " + hasInsert);
        
        assertTrue(hasDropTable || hasCreateTable, "备份文件应该包含有效的 SQL 语句");
        System.out.println("========== 测试 4 完成 ==========\n");
    }
}
