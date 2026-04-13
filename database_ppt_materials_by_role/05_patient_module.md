# 医院管理系统数据库设计 - 患者模块

## 1. 模块概述

患者模块是医院管理系统的用户端核心模块，负责患者的预约挂号、个人信息管理、病历查询和费用缴纳等功能。患者作为系统的服务对象，通过该模块可以便捷地获取医疗服务。

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

### 2.2 患者表 (patients)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个患者对应一个用户账号） |
| medical_record_number | VARCHAR | UNIQUE | 病历号（唯一） |
| allergies | TEXT | | 过敏药物记录 |
| emergency_contact | VARCHAR | | 紧急联系人 |
| emergency_phone | VARCHAR | | 紧急联系电话 |

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

### 2.8 账单表 (billing)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| appointment_id | BIGINT | FK | 外键，关联 appointments 表 |
| type | VARCHAR | | 账单类型（如 '挂号费', '药费', '检查费', '治疗费'） |
| amount | DECIMAL(10,2) | | 金额 |
| status | VARCHAR | | 账单状态（如 '未支付', '已支付'） |
| create_time | DATETIME | | 创建时间 |


## 4. 业务流程

### 4.1 预约挂号流程

1. 患者登录系统
2. 浏览科室和医生信息
3. 选择合适的医生和时间
4. 填写症状描述
5. 提交预约申请
6. 系统生成预约记录到 appointments 表
7. 患者收到预约确认

### 4.2 诊疗记录查询流程

1. 患者登录系统
2. 进入个人中心
3. 选择查看诊疗记录
4. 系统查询相关数据表（medical_records, prescriptions, checkups, advices）
5. 显示查询结果

### 4.3 费用缴纳流程

1. 患者登录系统
2. 查看待支付账单
3. 选择要支付的账单
4. 选择支付方式
5. 完成支付
6. 系统更新账单状态到 billing 表
7. 患者收到支付确认

## 5. 数据关联

### 5.1 表间关联

- **users ↔ patients**: 一对一关系，每个患者对应一个用户账号
- **patients ↔ appointments**: 一对多关系，一个患者可以有多条预约记录
- **patients ↔ medical_records**: 一对多关系，一个患者可以有多条病历记录
- **patients ↔ prescriptions**: 一对多关系，一个患者可以有多条处方记录
- **patients ↔ checkups**: 一对多关系，一个患者可以有多条检查记录
- **patients ↔ advices**: 一对多关系，一个患者可以有多条医嘱记录
- **patients ↔ billing**: 一对多关系，一个患者可以有多条账单记录

### 5.2 关联示例

```sql
-- 查询患者的预约记录
SELECT a.id AS appointment_id, a.appointment_time, a.status, a.symptoms,
       d.name AS department_name,
       CONCAT(du.name, '(', doc.title, ')') AS doctor_name
FROM appointments a
JOIN departments d ON a.department_id = d.id
JOIN doctors doc ON a.doctor_id = doc.id
JOIN users du ON doc.user_id = du.id
WHERE a.patient_id = 1
ORDER BY a.appointment_time DESC;

-- 查询患者的完整诊疗记录
SELECT mr.id AS record_id, mr.chief_complaint, mr.diagnosis_result, mr.create_time,
       pr.drug_list, pr.usage,
       ch.type, ch.result,
       ad.content
FROM medical_records mr
LEFT JOIN prescriptions pr ON mr.patient_id = pr.patient_id AND mr.appointment_id = pr.appointment_id
LEFT JOIN checkups ch ON mr.patient_id = ch.patient_id AND mr.appointment_id = ch.appointment_id
LEFT JOIN advices ad ON mr.patient_id = ad.patient_id AND mr.appointment_id = ad.appointment_id
WHERE mr.patient_id = 1
ORDER BY mr.create_time DESC;

-- 查询患者的账单记录
SELECT b.id AS bill_id, b.type, b.amount, b.status, b.create_time
FROM billing b
WHERE b.patient_id = 1
ORDER BY b.create_time DESC;
```

## 6. 安全考虑

### 6.1 数据隐私

- 患者只能访问自己的个人数据和诊疗记录
- 敏感医疗信息需要加密存储
- 数据传输需要加密

### 6.2 账户安全

- 密码加密存储
- 登录认证和授权
- 异常登录检测

## 7. 性能优化

### 7.1 索引设计

- 为 appointments.patient_id、appointments.status 创建索引
- 为 medical_records.patient_id 创建索引
- 为 prescriptions.patient_id 创建索引
- 为 checkups.patient_id 创建索引
- 为 advices.patient_id 创建索引
- 为 billing.patient_id、billing.status 创建索引

### 7.2 查询优化

- 使用索引加速患者数据查询
- 合理使用缓存减少数据库负载
- 分页查询减少数据传输

## 8. 扩展性考虑

### 8.1 未来扩展

- 支持在线咨询功能
- 支持健康档案管理
