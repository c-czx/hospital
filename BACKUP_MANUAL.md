# 数据库备份与恢复功能说明

## 功能概述

本系统实现了完整的数据库备份与恢复功能，管理员可以通过 Web 界面轻松管理数据库备份。

## 技术实现

### 1. 核心组件

#### 实体类：Backup
- **位置**: `com.hospital.entity.Backup`
- **说明**: 记录每次备份的元数据
- **字段**:
  - `id`: 备份记录 ID
  - `backupType`: 备份类型（FULL/INCREMENTAL）
  - `filePath`: 备份文件路径
  - `fileSize`: 文件大小（字节）
  - `description`: 备份说明
  - `backupTime`: 备份时间
  - `status`: 备份状态（SUCCESS/FAILED）
  - `remarks`: 备注信息

#### 数据访问层：BackupRepository
- **位置**: `com.hospital.repository.BackupRepository`
- **说明**: 提供备份记录的 CRUD 操作
- **方法**:
  - `findAllByOrderByBackupTimeDesc()`: 按时间倒序查询所有备份
  - `findByBackupTypeOrderByBackupTimeDesc()`: 按类型查询
  - `findByStatusOrderByBackupTimeDesc()`: 按状态查询

#### 服务层：BackupService
- **位置**: `com.hospital.service.BackupService`
- **说明**: 实现备份恢复的核心业务逻辑
- **主要方法**:
  - `createBackup()`: 创建数据库备份
  - `restoreDatabase()`: 恢复数据库
  - `getAllBackups()`: 获取所有备份记录
  - `deleteBackup()`: 删除备份记录及文件
  - `getBackupFile()`: 获取备份文件
  - `formatFileSize()`: 格式化文件大小显示

#### 控制器：DataController
- **位置**: `com.hospital.controller.DataController`
- **说明**: 处理备份恢复的 HTTP 请求
- **接口**:
  - `GET /admin/data`: 显示数据管理页面
  - `POST /admin/data/backup`: 执行备份
  - `POST /admin/data/restore/{id}`: 恢复数据
  - `GET /admin/data/download/{id}`: 下载备份
  - `POST /admin/data/delete/{id}`: 删除备份

## 使用方法

### 前提条件

1. **MySQL 安装**: 系统必须安装 MySQL 数据库
2. **mysqldump 工具**: 确保 `mysqldump` 命令可用
   - 如果 MySQL 已添加到系统环境变量，可直接使用
   - 否则需要修改 `BackupService.java` 中的 `MYSQL_DUMP_COMMAND` 常量，指定完整路径
3. **数据库配置**: 确保 `application.properties` 中的数据库配置正确

### 备份数据库

1. 登录管理员账户
2. 进入"数据管理"页面（`/admin/data`）
3. 在"数据备份"卡片中：
   - 选择备份类型：
     - **完整备份**: 备份整个数据库
     - **增量备份**: 仅备份变化的数据（当前实现与完整备份相同）
   - 填写备份说明（可选）
4. 点击"开始备份"按钮
5. 系统会：
   - 调用 `mysqldump` 工具导出数据库
   - 将备份文件保存到 `backups/` 目录
   - 记录备份信息到数据库
   - 显示备份结果

### 恢复数据库

1. 在"备份列表"中找到要恢复的备份记录
2. 确认备份状态为"成功"
3. 点击"恢复"按钮
4. 确认恢复操作（会弹出确认对话框）
5. 系统会：
   - 读取备份文件
   - 调用 `mysql` 命令导入数据
   - 覆盖当前数据库内容
   - 显示恢复结果

### 下载备份

1. 在"备份列表"中找到要下载的备份
2. 点击"下载"按钮
3. 浏览器会下载 SQL 备份文件

### 删除备份

1. 在"备份列表"中找到要删除的备份
2. 点击"删除"按钮
3. 确认删除操作
4. 系统会：
   - 删除备份文件
   - 删除备份记录

## 备份文件管理

### 存储位置

备份文件存储在项目的 `backups/` 目录下，文件名格式：
```
backup_<type>_<timestamp>.sql
```
例如：
- `backup_full_20240115_143022.sql`
- `backup_incremental_20240116_091530.sql`

### 备份文件格式

备份文件是标准的 MySQL SQL 脚本，包含：
- 数据库表结构
- 数据插入语句
- 字符集设置（utf8mb4）

可以用任何文本编辑器或数据库工具打开查看。

## 配置说明

### 修改 MySQL 命令路径

如果 MySQL 未添加到环境变量，需要修改 `BackupService.java`:

```java
// 修改前
private static final String MYSQL_DUMP_COMMAND = "mysqldump";
private static final String MYSQL_COMMAND = "mysql";

// 修改后（指定完整路径）
private static final String MYSQL_DUMP_COMMAND = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";
private static final String MYSQL_COMMAND = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe";
```

### 修改备份目录

```java
// 修改前
private static final String BACKUP_DIRECTORY = "backups";

// 修改后
private static final String BACKUP_DIRECTORY = "D:\\MyBackups";
```

## 注意事项

### 安全警告

1. **恢复操作不可逆**: 恢复数据库会覆盖当前所有数据，请谨慎操作
2. **备份前确认**: 恢复前务必确认备份文件的完整性和正确性
3. **定期备份**: 建议定期进行数据备份，防止数据丢失

### 性能考虑

1. **备份时间**: 完整备份的时间取决于数据库大小
2. **并发访问**: 备份期间数据库仍可访问，但可能影响性能
3. **磁盘空间**: 定期清理旧的备份文件，释放磁盘空间

### 故障排查

#### 备份失败

可能原因：
1. MySQL 未安装或 `mysqldump` 不可用
2. 数据库连接配置错误
3. 磁盘空间不足
4. 权限问题

解决方法：
1. 检查 MySQL 安装和环境变量配置
2. 验证 `application.properties` 中的数据库配置
3. 检查磁盘空间
4. 以管理员身份运行应用程序

#### 恢复失败

可能原因：
1. 备份文件不存在或损坏
2. MySQL 服务未运行
3. 数据库连接问题
4. 备份文件版本不兼容

解决方法：
1. 检查备份文件是否存在
2. 确保 MySQL 服务正在运行
3. 验证数据库连接
4. 确认 MySQL 版本兼容性

## 扩展功能建议

### 当前实现的功能

✅ 完整数据库备份
✅ 备份记录管理
✅ 数据恢复
✅ 备份文件下载
✅ 备份文件删除
✅ 备份状态监控

### 未来可以添加的功能

💡 增量备份的真正实现
💡 定时自动备份
💡 备份文件压缩
💡 远程备份（云存储）
💡 备份验证功能
💡 多数据库支持
💡 备份历史记录统计
💡 恢复前的预览功能

## 代码示例

### 手动创建备份

```java
@Autowired
private BackupService backupService;

// 创建备份
Backup backup = backupService.createBackup("FULL", "手动备份测试");
System.out.println("备份结果：" + backup.getStatus());
```

### 获取所有备份

```java
List<Backup> backups = backupService.getAllBackups();
for (Backup backup : backups) {
    System.out.println("备份 ID: " + backup.getId());
    System.out.println("备份类型：" + backup.getBackupType());
    System.out.println("备份时间：" + backup.getBackupTime());
    System.out.println("文件大小：" + backupService.formatFileSize(backup.getFileSize()));
}
```

### 恢复数据库

```java
Long backupId = 1L;
String result = backupService.restoreDatabase(backupId);
System.out.println("恢复结果：" + result);
```

## 技术支持

如有问题，请检查：
1. 应用程序日志
2. MySQL 错误日志
3. 备份文件是否存在
4. 数据库连接状态

---

**版权声明**: 本代码由 Muyvge 编写，仅用于学习交流，不得用于商业用途。
