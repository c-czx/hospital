# 医院管理系统数据库设计 - 医生模块

## 1. 模块概述

医生模块是医院管理系统的核心业务模块，负责患者的诊疗过程，包括病历记录、处方开具、检查项目申请和医嘱下达等功能。医生作为医疗服务的提供者，是系统中重要的角色之一。

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

### 2.2 医生表 (doctors)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个医生对应一个用户账号） |
| department_id | BIGINT | FK, NOT NULL | 外键，关联 departments 表 |
| title | VARCHAR | | 职称（如主任医师、主治医师） |
| specialty | VARCHAR | | 专业特长 |
| schedule | VARCHAR | | 排班信息 |

### 2.3 预约表 (appointments)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| department_id | BIGINT | FK, NOT NULL | 外键，关联 departments 表 |
| appointment_time | DATETIME | | 预约时间 |
| status | VARCHAR | | 预约状态（如 'pending', 'confirmed', 'cancelled'） |
| symptoms | TEXT | | 主诉/症状描述 |
| create_time | DATETIME | | 创建时间 |

### 2.4 病例表 (medical_records)

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

### 2.5 处方表 (prescriptions)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| appointment_id | BIGINT | FK | 外键，关联 appointments 表 |
| drug_list | TEXT | | 药品清单 |
| usage | TEXT | | 用法用量 |
| create_time | DATETIME | | 创建时间 |

### 2.6 检查项目表 (checkups)

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

### 2.7 医嘱表 (advices)

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

### 2.8 药品表 (drugs)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| name | VARCHAR | NOT NULL | 药品名称 |
| price | DECIMAL(10,2) | NOT NULL | 药品价格 |
| unit | VARCHAR | | 单位（如：盒、瓶、支） |

### 2.9 检查项目价格表 (checkup_items)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| name | VARCHAR | NOT NULL | 检查项目名称 |
| price | DECIMAL(10,2) | NOT NULL | 检查项目价格 |
| description | TEXT | | 检查项目描述 |

## 3. 核心功能

### 3.1 患者诊疗

- **预约管理**
  - 查看预约患者列表
  - 确认或取消预约
  - 查看患者预约详情

- **病历管理**
  - 创建和编辑病历记录
  - 记录主诉、现病史、诊断结果
  - 查看患者历史病历

- **诊断处理**
  - 记录患者生命体征
  - 做出诊断结论
  - 制定治疗方案

### 3.2 处方管理

- **开具处方**
  - 选择药品
  - 设置用法用量
  - 生成处方记录

- **处方查询**
  - 查看患者历史处方
  - 查看处方执行情况

### 3.3 检查管理

- **开具检查项目**
  - 选择检查类型
  - 填写检查要求
  - 生成检查单

- **检查结果查看**
  - 查看检查结果
  - 根据检查结果调整诊断

### 3.4 医嘱管理

- **下达医嘱**
  - 填写医嘱内容
  - 设置医嘱执行时间
  - 生成医嘱记录

- **医嘱查询**
  - 查看患者历史医嘱
  - 查看医嘱执行情况

## 4. 业务流程

### 4.1 诊疗流程

1. 医生登录系统
2. 查看当日预约患者列表
3. 患者就诊时，医生查看患者基本信息
4. 医生询问病情，记录主诉和现病史
5. 医生进行检查，记录生命体征
6. 医生做出诊断，记录诊断结果
7. 医生根据需要开具处方、检查项目和医嘱
8. 系统更新相应的数据库表

### 4.2 处方开具流程

1. 医生在诊疗过程中需要开具处方
2. 医生从药品库中选择药品
3. 医生设置药品的用法用量
4. 系统生成处方记录到 prescriptions 表
5. 患者凭处方到药房取药

### 4.3 检查申请流程

1. 医生在诊疗过程中需要开具检查项目
2. 医生从检查项目库中选择检查类型
3. 医生填写检查要求和说明
4. 系统生成检查记录到 checkups 表
5. 患者到检查科室进行检查
6. 检查科室完成检查并记录结果

### 4.4 医嘱下达流程

1. 医生在诊疗过程中需要下达医嘱
2. 医生填写医嘱内容和执行要求
3. 系统生成医嘱记录到 advices 表
4. 护士查看并执行医嘱
5. 系统更新医嘱状态

## 5. 数据关联

### 5.1 表间关联

- **users ↔ doctors**: 一对一关系，每个医生对应一个用户账号
- **doctors ↔ appointments**: 一对多关系，一个医生可以有多条预约记录
- **doctors ↔ medical_records**: 一对多关系，一个医生可以为多个患者创建病历
- **doctors ↔ prescriptions**: 一对多关系，一个医生可以为多个患者开具处方
- **doctors ↔ checkups**: 一对多关系，一个医生可以为多个患者开具检查项目
- **doctors ↔ advices**: 一对多关系，一个医生可以为多个患者下达医嘱

### 5.2 关联示例

```sql
-- 查询医生的预约患者列表
SELECT a.id AS appointment_id, a.appointment_time, a.status, a.symptoms,
       p.medical_record_number, u.name AS patient_name
FROM appointments a
JOIN patients p ON a.patient_id = p.id
JOIN users u ON p.user_id = u.id
WHERE a.doctor_id = 1 AND a.status = 'confirmed'
ORDER BY a.appointment_time;

-- 查询患者的诊疗记录
SELECT mr.id AS record_id, mr.chief_complaint, mr.present_illness, mr.diagnosis_result,
       mr.create_time,
       pr.drug_list, pr.usage,
       ch.type, ch.description, ch.result,
       ad.content
FROM medical_records mr
LEFT JOIN prescriptions pr ON mr.appointment_id = pr.appointment_id
LEFT JOIN checkups ch ON mr.appointment_id = ch.appointment_id
LEFT JOIN advices ad ON mr.appointment_id = ad.appointment_id
WHERE mr.patient_id = 1 AND mr.doctor_id = 1
ORDER BY mr.create_time DESC;

-- 查询医生的工作量统计
SELECT COUNT(mr.id) AS medical_record_count,
       COUNT(pr.id) AS prescription_count,
       COUNT(ch.id) AS checkup_count,
       COUNT(ad.id) AS advice_count
FROM doctors d
LEFT JOIN medical_records mr ON d.id = mr.doctor_id
LEFT JOIN prescriptions pr ON d.id = pr.doctor_id
LEFT JOIN checkups ch ON d.id = ch.doctor_id
LEFT JOIN advices ad ON d.id = ad.doctor_id
WHERE d.id = 1;
```

## 6. 安全考虑

### 6.1 数据隐私

- 医生只能访问和修改自己的患者数据
- 患者诊疗记录属于敏感信息，需要严格保护
- 数据传输和存储需要加密

### 6.2 数据完整性

- 使用外键约束确保数据关联的完整性
- 诊疗记录一旦创建，不得随意删除，只能标记为无效

## 7. 性能优化

### 7.1 索引设计

- 为 appointments.doctor_id、appointments.status 创建索引
- 为 medical_records.doctor_id、medical_records.patient_id 创建索引
- 为 prescriptions.doctor_id、prescriptions.patient_id 创建索引
- 为 checkups.doctor_id、checkups.patient_id 创建索引
- 为 advices.doctor_id、advices.patient_id 创建索引

### 7.2 查询优化

- 使用索引加速诊疗记录查询
- 合理使用缓存减少数据库负载
- 定期归档历史诊疗记录

## 8. 扩展性考虑

### 8.1 未来扩展

- 支持电子签名功能
- 支持医学影像管理
- 支持远程诊疗记录
- 支持与医保系统集成

### 8.2 数据迁移

- 设计合理的数据迁移方案，确保系统升级时数据的完整性
- 提供数据导入/导出功能，便于与其他系统集成