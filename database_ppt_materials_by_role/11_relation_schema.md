# 医院挂号系统 - 数据库关系模式

## 1. 关系模式概述

本系统共包含 15 个数据表，分为基础数据表、业务数据表和辅助数据表三大类。以下是各表的关系模式详细说明。

## 2. 基础数据表关系模式

### 2.1 用户表 (users)

**关系模式**：
```
users(id, name, age, gender, phone, email, password, role)
```

**主键**：`id`

**候选键**：`phone`, `email`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `phone`: VARCHAR, 唯一
- `email`: VARCHAR, 唯一
- `role`: VARCHAR, 取值范围 ('admin', 'doctor', 'nurse', 'patient')

**语义说明**：存储系统用户的基本信息和账号信息，role 字段区分用户角色

---

### 2.2 科室表 (departments)

**关系模式**：
```
departments(id, name, description, location)
```

**主键**：`id`

**候选键**：`name`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `name`: VARCHAR, 唯一，非空
- `description`: TEXT
- `location`: VARCHAR

**语义说明**：存储医院的科室信息

---

### 2.3 药品表 (drugs)

**关系模式**：
```
drugs(id, name, price, unit)
```

**主键**：`id`

**候选键**：`name`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `name`: VARCHAR, 非空
- `price`: DECIMAL(10,2), 非空
- `unit`: VARCHAR

**语义说明**：存储药品信息及价格

---

### 2.4 检查项目价格表 (checkup_items)

**关系模式**：
```
checkup_items(id, name, price, description)
```

**主键**：`id`

**候选键**：`name`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `name`: VARCHAR, 非空
- `price`: DECIMAL(10,2), 非空
- `description`: TEXT

**语义说明**：存储各类检查项目的名称及价格

---

## 3. 业务数据表关系模式

### 3.1 医生表 (doctors)

**关系模式**：
```
doctors(id, user_id, department_id, title, specialty, schedule)
```

**主键**：`id`

**外键**：
- `user_id` → `users(id)`
- `department_id` → `departments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `user_id`: BIGINT, 外键，非空，唯一
- `department_id`: BIGINT, 外键，非空
- `title`: VARCHAR
- `specialty`: VARCHAR
- `schedule`: VARCHAR

**语义说明**：存储医生的详细信息，关联用户表和科室表

---

### 3.2 护士表 (nurses)

**关系模式**：
```
nurses(id, user_id, department_id, ward, title)
```

**主键**：`id`

**外键**：
- `user_id` → `users(id)`
- `department_id` → `departments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `user_id`: BIGINT, 外键，非空，唯一
- `department_id`: BIGINT, 外键
- `ward`: VARCHAR(50)
- `title`: VARCHAR(50)

**语义说明**：存储护士的详细信息，关联用户表和科室表

---

### 3.3 患者表 (patients)

**关系模式**：
```
patients(id, user_id, medical_record_number, allergies, emergency_contact, emergency_phone)
```

**主键**：`id`

**外键**：
- `user_id` → `users(id)`

**候选键**：`medical_record_number`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `user_id`: BIGINT, 外键，非空，唯一
- `medical_record_number`: VARCHAR, 唯一
- `allergies`: TEXT
- `emergency_contact`: VARCHAR
- `emergency_phone`: VARCHAR

**语义说明**：存储患者的详细信息，关联用户表

---

### 3.4 排班表 (schedules)

**关系模式**：
```
schedules(id, doctor_id, start_time, end_time, total_number, remain_number, status)
```

**主键**：`id`

**外键**：
- `doctor_id` → `doctors(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `doctor_id`: BIGINT, 外键，非空
- `start_time`: DATETIME
- `end_time`: DATETIME
- `total_number`: INT
- `remain_number`: INT
- `status`: INT, 取值范围 (0-停诊，1-正常)

**语义说明**：定义医生的出诊时间安排

---

### 3.5 预约表 (appointments)

**关系模式**：
```
appointments(id, patient_id, doctor_id, department_id, appointment_time, status, symptoms, create_time)
```

**主键**：`id`

**外键**：
- `patient_id` → `patients(id)`
- `doctor_id` → `doctors(id)`
- `department_id` → `departments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `patient_id`: BIGINT, 外键，非空
- `doctor_id`: BIGINT, 外键，非空
- `department_id`: BIGINT, 外键，非空
- `appointment_time`: DATETIME
- `status`: VARCHAR, 取值范围 ('pending', 'confirmed', 'cancelled')
- `symptoms`: TEXT
- `create_time`: DATETIME

**语义说明**：记录患者的预约挂号信息

---

### 3.6 病例表 (medical_records)

**关系模式**：
```
medical_records(id, doctor_id, patient_id, appointment_id, chief_complaint, present_illness, 
                diagnosis_result, diagnosis_time, create_time, temperature, blood_pressure, nurse_notes)
```

**主键**：`id`

**外键**：
- `doctor_id` → `doctors(id)`
- `patient_id` → `patients(id)`
- `appointment_id` → `appointments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `doctor_id`: BIGINT, 外键，非空
- `patient_id`: BIGINT, 外键，非空
- `appointment_id`: BIGINT, 外键
- `chief_complaint`: TEXT
- `present_illness`: TEXT
- `diagnosis_result`: TEXT
- `diagnosis_time`: DATETIME
- `create_time`: DATETIME
- `temperature`: DOUBLE
- `blood_pressure`: INT
- `nurse_notes`: TEXT

**语义说明**：记录医生为患者诊疗的详细记录

---

### 3.7 处方表 (prescriptions)

**关系模式**：
```
prescriptions(id, doctor_id, patient_id, appointment_id, drug_list, usage, create_time)
```

**主键**：`id`

**外键**：
- `doctor_id` → `doctors(id)`
- `patient_id` → `patients(id)`
- `appointment_id` → `appointments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `doctor_id`: BIGINT, 外键，非空
- `patient_id`: BIGINT, 外键，非空
- `appointment_id`: BIGINT, 外键
- `drug_list`: TEXT
- `usage`: TEXT
- `create_time`: DATETIME

**语义说明**：记录医生为患者开具的处方信息

---

### 3.8 检查项目表 (checkups)

**关系模式**：
```
checkups(id, doctor_id, patient_id, appointment_id, type, description, result, status, create_time, update_time)
```

**主键**：`id`

**外键**：
- `doctor_id` → `doctors(id)`
- `patient_id` → `patients(id)`
- `appointment_id` → `appointments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `doctor_id`: BIGINT, 外键，非空
- `patient_id`: BIGINT, 外键，非空
- `appointment_id`: BIGINT, 外键
- `type`: VARCHAR
- `description`: TEXT
- `result`: TEXT
- `status`: INT, 取值范围 (0-待检查，1-已完成)
- `create_time`: DATETIME
- `update_time`: DATETIME

**语义说明**：记录医生为患者开具的检查项目

---

### 3.9 医嘱表 (advices)

**关系模式**：
```
advices(id, doctor_id, patient_id, appointment_id, content, status, create_time, update_time)
```

**主键**：`id`

**外键**：
- `doctor_id` → `doctors(id)`
- `patient_id` → `patients(id)`
- `appointment_id` → `appointments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `doctor_id`: BIGINT, 外键，非空
- `patient_id`: BIGINT, 外键，非空
- `appointment_id`: BIGINT, 外键
- `content`: TEXT
- `status`: INT, 取值范围 (0-未读，1-已读)
- `create_time`: DATETIME
- `update_time`: DATETIME

**语义说明**：记录医生给患者的医嘱信息

---

### 3.10 账单表 (billing)

**关系模式**：
```
billing(id, patient_id, appointment_id, type, amount, status, create_time)
```

**主键**：`id`

**外键**：
- `patient_id` → `patients(id)`
- `appointment_id` → `appointments(id)`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `patient_id`: BIGINT, 外键，非空
- `appointment_id`: BIGINT, 外键
- `type`: VARCHAR, 取值范围 ('挂号费', '药费', '检查费', '治疗费')
- `amount`: DECIMAL(10,2)
- `status`: VARCHAR, 取值范围 ('未支付', '已支付')
- `create_time`: DATETIME

**语义说明**：存储患者的缴费账单信息

---

### 3.11 备份记录表 (backups)

**关系模式**：
```
backups(id, backup_type, file_path, file_size, description, backup_time, status, remarks)
```

**主键**：`id`

**约束条件**：
- `id`: BIGINT, 主键，自增
- `backup_type`: VARCHAR(20), 非空，取值范围 ('FULL', 'INCREMENTAL')
- `file_path`: VARCHAR(500), 非空
- `file_size`: BIGINT, 非空
- `description`: VARCHAR(500)
- `backup_time`: DATETIME, 非空
- `status`: VARCHAR(20), 非空，取值范围 ('SUCCESS', 'FAILED')
- `remarks`: VARCHAR(1000)

**语义说明**：记录数据库备份的详细信息

---

## 4. 表间关系说明

### 4.1 一对一关系 (1:1)

1. **users ↔ doctors**
   - 每个医生对应一个用户账号
   - 关系：`doctors.user_id` → `users.id`

2. **users ↔ nurses**
   - 每个护士对应一个用户账号
   - 关系：`nurses.user_id` → `users.id`

3. **users ↔ patients**
   - 每个患者对应一个用户账号
   - 关系：`patients.user_id` → `users.id`

4. **appointments ↔ medical_records**
   - 一次预约对应一条病历记录
   - 关系：`medical_records.appointment_id` → `appointments.id`

5. **appointments ↔ prescriptions**
   - 一次预约对应一条处方记录
   - 关系：`prescriptions.appointment_id` → `appointments.id`

6. **appointments ↔ checkups**
   - 一次预约对应一条检查记录
   - 关系：`checkups.appointment_id` → `appointments.id`

7. **appointments ↔ advices**
   - 一次预约对应一条医嘱记录
   - 关系：`advices.appointment_id` → `appointments.id`

---

### 4.2 一对多关系 (1:N)

1. **departments ↔ doctors**
   - 一个科室有多个医生
   - 关系：`doctors.department_id` → `departments.id`

2. **departments ↔ nurses**
   - 一个科室有多个护士
   - 关系：`nurses.department_id` → `departments.id`

3. **departments ↔ appointments**
   - 一个科室有多条预约记录
   - 关系：`appointments.department_id` → `departments.id`

4. **doctors ↔ schedules**
   - 一个医生有多个排班记录
   - 关系：`schedules.doctor_id` → `doctors.id`

5. **doctors ↔ appointments**
   - 一个医生有多条预约记录
   - 关系：`appointments.doctor_id` → `doctors.id`

6. **doctors ↔ medical_records**
   - 一个医生创建多条病历记录
   - 关系：`medical_records.doctor_id` → `doctors.id`

7. **doctors ↔ prescriptions**
   - 一个医生开具多条处方
   - 关系：`prescriptions.doctor_id` → `doctors.id`

8. **doctors ↔ checkups**
   - 一个医生开具多个检查项目
   - 关系：`checkups.doctor_id` → `doctors.id`

9. **doctors ↔ advices**
   - 一个医生下达多条医嘱
   - 关系：`advices.doctor_id` → `doctors.id`

10. **patients ↔ appointments**
    - 一个患者有多条预约记录
    - 关系：`appointments.patient_id` → `patients.id`

11. **patients ↔ medical_records**
    - 一个患者有多条病历记录
    - 关系：`medical_records.patient_id` → `patients.id`

12. **patients ↔ prescriptions**
    - 一个患者有多条处方记录
    - 关系：`prescriptions.patient_id` → `patients.id`

13. **patients ↔ checkups**
    - 一个患者有多条检查记录
    - 关系：`checkups.patient_id` → `patients.id`

14. **patients ↔ advices**
    - 一个患者有多条医嘱记录
    - 关系：`advices.patient_id` → `patients.id`

15. **patients ↔ billing**
    - 一个患者有多条账单记录
    - 关系：`billing.patient_id` → `patients.id`

---

## 5. 规范化分析

### 5.1 第一范式 (1NF)
所有表的字段都是原子值，不可再分，满足第一范式。

### 5.2 第二范式 (2NF)
所有非主属性完全依赖于主键，不存在部分依赖，满足第二范式。

### 5.3 第三范式 (3NF)
所有非主属性不传递依赖于主键，满足第三范式。

---

## 6. 数据完整性约束

### 6.1 实体完整性
- 所有表的主键不能为空且必须唯一

### 6.2 参照完整性
- 所有外键必须引用存在的主键值或为空

### 6.3 用户定义完整性
- 唯一性约束：如 `users.phone`, `users.email`, `departments.name` 等
- 检查约束：如 `schedules.status`, `appointments.status` 等
- 非空约束：如 `departments.name`, `drugs.price` 等

---

## 7. 索引设计

### 7.1 主键索引
所有表的主键自动创建聚簇索引。

### 7.2 唯一索引
- `users(phone)`
- `users(email)`
- `departments(name)`
- `patients(medical_record_number)`

### 7.3 普通索引
- `appointments(patient_id)`
- `appointments(doctor_id)`
- `appointments(department_id)`
- `medical_records(patient_id)`
- `medical_records(doctor_id)`
- `prescriptions(patient_id)`
- `checkups(patient_id)`
- `advices(patient_id)`
- `billing(patient_id)`

---

## 8. 视图设计（可选）

### 8.1 医生信息视图
```sql
CREATE VIEW v_doctor_info AS
SELECT d.id, u.name, u.phone, u.email, d.title, d.specialty, dept.name AS department_name
FROM doctors d
JOIN users u ON d.user_id = u.id
JOIN departments dept ON d.department_id = dept.id;
```

### 8.2 患者信息视图
```sql
CREATE VIEW v_patient_info AS
SELECT p.id, u.name, u.phone, u.email, p.medical_record_number, p.allergies
FROM patients p
JOIN users u ON p.user_id = u.id;
```

### 8.3 预约详情视图
```sql
CREATE VIEW v_appointment_detail AS
SELECT a.id, a.appointment_time, a.status,
       p.medical_record_number, pu.name AS patient_name,
       d.title AS doctor_title, du.name AS doctor_name,
       dept.name AS department_name
FROM appointments a
JOIN patients p ON a.patient_id = p.id
JOIN users pu ON p.user_id = pu.id
JOIN doctors d ON a.doctor_id = d.id
JOIN users du ON d.user_id = du.id
JOIN departments dept ON a.department_id = dept.id;
```
