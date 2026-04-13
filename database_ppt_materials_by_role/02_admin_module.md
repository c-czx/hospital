# 医院管理系统数据库设计 - 管理员模块

## 1. 模块概述

管理员模块是医院管理系统的核心管理模块，负责系统的整体管理和维护，包括用户管理、科室管理、数据备份等功能。管理员作为系统的最高权限角色，拥有对系统所有功能的访问和管理权限。

## 2. 涉及数据表

### 2.1 用户表 (users)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| name | VARCHAR | | 姓名 |
| age | INT | | 年龄 |
| gender | VARCHAR | | 性别 |
| phone | VARCHAR | | 手机号 |
| email | VARCHAR | | 邮箱 |
| password | VARCHAR | | 密码（加密存储） |
| role | VARCHAR | | 用户角色（如 'patient', 'doctor', 'nurse', 'admin'） |

### 2.2 科室表 (departments)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| name | VARCHAR | NOT NULL, UNIQUE | 科室名称（唯一） |
| description | TEXT | | 科室描述 |
| location | VARCHAR | | 科室楼层位置 |

### 2.3 医生表 (doctors)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个医生对应一个用户账号） |
| department_id | BIGINT | FK, NOT NULL | 外键，关联 departments 表 |
| title | VARCHAR | | 职称（如主任医师、主治医师） |
| specialty | VARCHAR | | 专业特长 |
| schedule | VARCHAR | | 排班信息 |

### 2.4 护士表 (nurses)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个护士对应一个用户账号） |
| department_id | BIGINT | FK | 外键，关联 departments 表（所属科室） |
| ward | VARCHAR(50) | | 负责病区 |
| title | VARCHAR(50) | | 职称 |

### 2.5 患者表 (patients)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个患者对应一个用户账号） |
| medical_record_number | VARCHAR | UNIQUE | 病历号（唯一） |
| allergies | TEXT | | 过敏药物记录 |
| emergency_contact | VARCHAR | | 紧急联系人 |
| emergency_phone | VARCHAR | | 紧急联系电话 |

### 2.6 备份记录表 (backups)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| backup_type | VARCHAR(20) | NOT NULL | 备份类型（FULL-完整备份，INCREMENTAL-增量备份） |
| file_path | VARCHAR(500) | NOT NULL | 备份文件路径 |
| file_size | BIGINT | NOT NULL | 备份文件大小（字节） |
| description | VARCHAR(500) | | 备份说明 |
| backup_time | DATETIME | NOT NULL | 备份时间 |
| status | VARCHAR(20) | NOT NULL | 备份状态（SUCCESS-成功，FAILED-失败） |
| remarks | VARCHAR(1000) | | 备注信息（如失败原因等） |

## 4. 业务流程

### 4.1 用户管理流程

1. 管理员登录系统
2. 进入用户管理模块
3. 选择操作类型（添加、编辑、删除）
4. 执行相应操作
5. 系统更新 users 表
6. 根据用户角色，更新对应角色表（doctors、nurses、patients）

### 4.2 科室管理流程

1. 管理员登录系统
2. 进入科室管理模块
3. 选择操作类型（添加、编辑、删除）
4. 执行相应操作
5. 系统更新 departments 表

### 4.3 数据备份流程

1. 管理员设置备份策略
2. 系统按照策略自动执行备份
3. 系统创建备份记录到 backups 表
4. 管理员查看备份状态和历史

## 5. 数据关联

### 5.1 表间关联

- **users ↔ doctors**: 一对一关系，每个医生对应一个用户账号
- **users ↔ nurses**: 一对一关系，每个护士对应一个用户账号
- **users ↔ patients**: 一对一关系，每个患者对应一个用户账号
- **departments ↔ doctors**: 一对多关系，一个科室可以有多个医生
- **departments ↔ nurses**: 一对多关系，一个科室可以有多个护士

### 5.2 关联示例

```sql
-- 查询所有用户及其角色信息
SELECT u.id, u.name, u.role,
       CASE 
           WHEN u.role = 'doctor' THEN d.title
           WHEN u.role = 'nurse' THEN n.title
           WHEN u.role = 'patient' THEN p.medical_record_number
           ELSE NULL
       END AS role_info
FROM users u
LEFT JOIN doctors d ON u.id = d.user_id AND u.role = 'doctor'
LEFT JOIN nurses n ON u.id = n.user_id AND u.role = 'nurse'
LEFT JOIN patients p ON u.id = p.user_id AND u.role = 'patient';

-- 查询科室及其人员情况
SELECT d.name AS department_name,
       COUNT(doc.id) AS doctor_count,
       COUNT(n.id) AS nurse_count
FROM departments d
LEFT JOIN doctors doc ON d.id = doc.department_id
LEFT JOIN nurses n ON d.id = n.department_id
GROUP BY d.id;

-- 查询备份历史
SELECT backup_type, status, COUNT(*) AS count
FROM backups
GROUP BY backup_type, status;
```

## 6. 安全考虑

### 6.1 权限控制

- 管理员拥有最高权限，可以访问和管理所有系统功能
- 操作日志记录，便于审计和追踪
- 敏感操作需要二次确认

### 6.2 数据安全

- 密码加密存储
- 数据备份和恢复机制
- 定期安全审计

## 7. 性能优化

### 7.1 索引设计

- 为 users.phone、users.email 创建索引
- 为 departments.name 创建唯一索引
- 为 doctors.department_id、nurses.department_id 创建索引

### 7.2 查询优化

- 使用分页查询减少数据传输
- 合理使用 JOIN 语句，避免复杂查询
- 缓存热点数据，如科室信息、用户基本信息

## 8. 扩展性考虑

### 8.1 未来扩展

- 支持多医院管理：添加 hospital_id 字段
- 支持用户组管理：增加 user_groups 表
- 支持更细粒度的权限控制：增加 permissions 表
- 支持系统日志管理：增加 system_logs 表

### 8.2 数据迁移

- 设计合理的数据迁移方案，确保系统升级时数据的完整性
- 提供数据导入/导出功能，便于与其他系统集成