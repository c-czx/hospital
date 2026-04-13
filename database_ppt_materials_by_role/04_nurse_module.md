# 医院管理系统数据库设计 - 护士模块

## 1. 模块概述

护士模块是医院管理系统的重要组成部分，负责患者的护理工作，包括病历记录更新、生命体征监测、医嘱执行等功能。护士作为医疗团队的重要成员，在患者诊疗过程中发挥着关键作用。

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

### 2.2 护士表 (nurses)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个护士对应一个用户账号） |
| department_id | BIGINT | FK | 外键，关联 departments 表（所属科室） |
| ward | VARCHAR(50) | | 负责病区 |
| title | VARCHAR(50) | | 职称 |

### 2.3 病例表 (medical_records)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| appointment_id | BIGINT | FK | 外键，关联 appointments 表 |
| chief_complaint | TEXT | | 主诉 |
| present_illness | TEXT | | 现病史 |
| diagnosis_result | TEXT | | 诊断结果 |
| diagnosis_time | DATETIME | | 诊断时间 |
| create_time | DATETIME | | 创建时间 |
| temperature | DOUBLE | | 体温（℃） |
| blood_pressure | INT | | 血压（mmHg） |
| nurse_notes | TEXT | | 护士记录 |

### 2.4 医嘱表 (advices)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| appointment_id | BIGINT | FK | 外键，关联 appointments 表 |
| content | TEXT | | 医嘱内容 |
| status | INT | | 状态（如 0-未读，1-已读） |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

### 2.5 检查项目表 (checkups)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| appointment_id | BIGINT | FK | 外键，关联 appointments 表 |
| type | VARCHAR | | 检查类型（如体温、血压、胸片等） |
| description | TEXT | | 检查描述 |
| result | TEXT | | 检查结果 |
| status | INT | | 状态（0-待检查，1-已完成） |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

## 3. 核心功能

### 3.1 患者护理

- **生命体征监测**
  - 测量患者体温、血压等生命体征
  - 记录生命体征数据
  - 监测生命体征变化趋势

- **护理记录**
  - 记录患者护理情况
  - 记录患者饮食、睡眠等情况
  - 记录患者病情变化

- **患者信息管理**
  - 查看患者基本信息
  - 查看患者诊疗记录
  - 了解患者过敏史和特殊需求

### 3.2 病历记录更新

- **病历管理**
  - 查看患者病历
  - 更新患者生命体征数据
  - 添加护士护理记录

- **数据同步**
  - 确保病历数据的实时更新
  - 与医生共享患者护理信息

### 3.3 医嘱执行

- **医嘱查看**
  - 查看医生下达的医嘱
  - 了解医嘱执行要求和时间

- **医嘱执行**
  - 执行医嘱并记录执行情况
  - 更新医嘱状态
  - 向医生反馈医嘱执行结果

### 3.4 检查协助

- **检查准备**
  - 为患者准备检查前的准备工作
  - 向患者说明检查注意事项

- **检查协助**
  - 协助患者完成检查
  - 记录检查过程中的情况
  - 向医生反馈检查情况

## 4. 业务流程

### 4.1 护理流程

1. 护士登录系统
2. 查看负责病区的患者列表
3. 为患者测量生命体征
4. 更新患者病历记录
5. 添加护理记录
6. 系统更新 medical_records 表

### 4.2 医嘱执行流程

1. 护士查看医生下达的医嘱
2. 了解医嘱执行要求
3. 执行医嘱
4. 记录执行情况
5. 更新医嘱状态
6. 系统更新 advices 表

### 4.3 检查协助流程

1. 护士查看患者的检查项目
2. 为患者准备检查
3. 协助患者完成检查
4. 记录检查结果
5. 系统更新 checkups 表

## 5. 数据关联

### 5.1 表间关联

- **users ↔ nurses**: 一对一关系，每个护士对应一个用户账号
- **nurses ↔ medical_records**: 一对多关系，一个护士可以为多个患者更新病历
- **nurses ↔ advices**: 一对多关系，一个护士可以执行多个患者的医嘱
- **nurses ↔ checkups**: 一对多关系，一个护士可以协助多个患者完成检查

### 5.2 关联示例

```sql
-- 查询护士负责的患者列表
SELECT p.id AS patient_id, u.name AS patient_name, p.medical_record_number,
       mr.temperature, mr.blood_pressure, mr.nurse_notes,
       mr.create_time
FROM patients p
JOIN users u ON p.user_id = u.id
LEFT JOIN medical_records mr ON p.id = mr.patient_id
WHERE mr.nurse_notes IS NOT NULL
ORDER BY mr.create_time DESC;

-- 查询待执行的医嘱
SELECT ad.id AS advice_id, ad.content, ad.create_time,
       u.name AS patient_name, p.medical_record_number
FROM advices ad
JOIN patients p ON ad.patient_id = p.id
JOIN users u ON p.user_id = u.id
WHERE ad.status = 0
ORDER BY ad.create_time;

-- 查询护士的工作量统计
SELECT COUNT(DISTINCT mr.id) AS medical_record_updates,
       COUNT(DISTINCT ad.id) AS advices_executed,
       COUNT(DISTINCT ch.id) AS checkups_assisted
FROM medical_records mr
LEFT JOIN advices ad ON mr.patient_id = ad.patient_id
LEFT JOIN checkups ch ON mr.patient_id = ch.patient_id
WHERE mr.nurse_notes IS NOT NULL
  AND ad.status = 1
  AND ch.status = 1;
```

## 6. 安全考虑

### 6.1 数据隐私

- 护士只能访问和修改自己负责患者的数据
- 患者诊疗记录属于敏感信息，需要严格保护
- 数据传输和存储需要加密

### 6.2 数据完整性

- 确保护理记录的准确性和完整性
- 医嘱执行记录的不可篡改性

## 7. 性能优化

### 7.1 索引设计

- 为 medical_records.patient_id 创建索引
- 为 advices.patient_id、advices.status 创建索引
- 为 checkups.patient_id、checkups.status 创建索引

### 7.2 查询优化

- 使用索引加速患者数据查询
- 合理使用缓存减少数据库负载
- 定期归档历史护理记录

## 8. 扩展性考虑

### 8.1 未来扩展

- 支持护理计划管理
- 支持护理质量评估
- 支持移动护理终端
- 支持与智能设备集成

### 8.2 数据迁移

- 设计合理的数据迁移方案，确保系统升级时数据的完整性
- 提供数据导入/导出功能，便于与其他系统集成