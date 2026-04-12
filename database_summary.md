# 医院管理系统数据库表结构说明

## 数据库表关系图

```
+----------------+       +----------------+       +----------------+
|     users      |       | departments    |       |    doctors     |
+----------------+       +----------------+       +----------------+
| PK id          |<----->| PK id          |<----->| PK id          |
|    name        |       |    name        |       | FK user_id     |
|    age         |       |    description |       | FK department_ |
|    gender      |       +----------------+       |    id          |
|    phone       |                                |    title       |
|    email       |                                |    specialty   |
|    password    |                                |    schedule    |
|    role        |                                +----------------+
+----------------+                                      ^
      ^                                                 |
      |                                                 |
      |                                                 |
+----------------+       +----------------+       +----------------+
|  appointments  |       |   schedules    |       |medical_records |
+----------------+       +----------------+       +----------------+
| PK id          |       | PK id          |       | PK id          |
| FK user_id     |       | FK doctor_id   |       | FK doctor_id   |
| FK doctor_id   |       |    start_time  |       | FK user_id     |
| FK department_ |       |    end_time    |       |    chief_compl |
|    id          |       |    total_num   |       |    present_ill |
|    appoint_time|       |    remain_num  |       |    diagnosis   |
|    status      |       |    status      |       |    diag_time   |
|    symptoms    |       +----------------+       |    create_time |
|    create_time |                                +----------------+
+----------------+                                      ^
      ^                                                 |
      |                                                 |
      |                                                 |
+----------------+       +----------------+       +----------------+
| prescriptions  |       |    advices     |       |    billing     |
+----------------+       +----------------+       +----------------+
| PK id          |       | PK id          |       | PK id          |
| FK doctor_id   |       | FK doctor_id   |       | FK user_id     |
| FK user_id     |       | FK user_id     |       |    type        |
|    drug_list   |       |    content     |       |    amount      |
|    usage       |       |    status      |       |    status      |
|    create_time |       |    create_time |       |    create_time |
+----------------+       |    update_time |       +----------------+
                         +----------------+
                              ^
                              |
+----------------+            |
|    nurses      |            |
+----------------+            |
| PK id          |            |
| FK user_id     |<-----------+
|    nurse_name  |
|    phone       |
|    department  |
+----------------+

+----------------+       +----------------+       +----------------+
|    patients    |       | registrations  |       |    checkups    |
+----------------+       +----------------+       +----------------+
| PK id          |<----->| PK id          |       | PK id          |
| FK user_id     |       | FK patient_id  |       | FK doctor_id   |
|    medical_    |       | FK department_ |       | FK user_id     |
|    record_num  |       | FK doctor_id   |       |    type        |
|    allergies   |       |    reg_time    |       |    description |
|    emergency_  |       |    status      |       |    result      |
|    contact     |       |    fee         |       |    status      |
|    emergency_  |       |    queue_num   |       |    create_time |
|    phone       |       +----------------+       |    update_time |
+----------------+                                +----------------+
```

## 数据表详细说明

### 1. 用户表 (users)

存储系统用户的基本信息和账号信息。

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

### 2. 科室表 (departments)

存储医院的科室信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| name | VARCHAR | NOT NULL, UNIQUE | 科室名称（唯一） |
| description | TEXT | | 科室描述 |

### 3. 医生表 (doctors)

存储医生的详细信息，关联用户表和科室表。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个医生对应一个用户账号） |
| department_id | BIGINT | FK, NOT NULL | 外键，关联 departments 表 |
| title | VARCHAR | | 职称（如主任医师、主治医师） |
| specialty | VARCHAR | | 专业特长 |
| schedule | VARCHAR | | 排班信息 |

### 4. 护士表 (nurses)

存储护士的详细信息，关联用户表。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个护士对应一个用户账号） |
| phone | VARCHAR | | 联系电话 |

### 5. 患者表 (patients)

存储患者的详细信息，关联用户表。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表（每个患者对应一个用户账号） |
| medical_record_number | VARCHAR | UNIQUE | 病历号（唯一） |
| allergies | TEXT | | 过敏药物记录 |
| emergency_contact | VARCHAR | | 紧急联系人 |
| emergency_phone | VARCHAR | | 紧急联系电话 |

### 6. 挂号表 (registrations)

存储患者的挂号记录。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| patient_id | BIGINT | FK, NOT NULL | 外键，关联 patients 表 |
| department_id | BIGINT | FK, NOT NULL | 外键，关联 departments 表 |
| doctor_id | BIGINT | FK | 外键，关联 doctors 表（可选） |
| reg_time | DATETIME | NOT NULL | 挂号时间 |
| status | VARCHAR | | 挂号状态（0: 待缴费，1: 已缴费，2: 诊疗中，3: 已完成，4: 已退号） |
| fee | DECIMAL(10,2) | | 挂号费用 |
| queue_number | VARCHAR | | 排队号（如 A001） |

### 7. 排班表 (schedules)

定义医生的出诊时间安排。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| start_time | DATETIME | | 开始时间 |
| end_time | DATETIME | | 结束时间 |
| total_number | INT | | 总名额数 |
| remain_number | INT | | 剩余名额数 |
| status | INT | | 状态（如 0-停诊，1-正常） |

### 8. 预约表 (appointments)

记录用户的预约挂号信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| department_id | BIGINT | FK, NOT NULL | 外键，关联 departments 表 |
| appointment_time | DATETIME | | 预约时间 |
| status | VARCHAR | | 预约状态（如 'pending', 'confirmed', 'cancelled'） |
| symptoms | TEXT | | 主诉/症状描述 |
| create_time | DATETIME | | 创建时间 |

### 9. 就诊记录表 (medical_records)

记录医生为患者诊疗的详细记录。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表 |
| chief_complaint | TEXT | | 主诉 |
| present_illness | TEXT | | 现病史 |
| diagnosis_result | TEXT | | 诊断结果 |
| diagnosis_time | DATETIME | | 诊断时间 |
| create_time | DATETIME | | 创建时间 |
| temperature | DOUBLE | | 体温（℃） |
| blood_pressure | INT | | 血压（mmHg） |
| nurse_notes | TEXT | | 护士记录 |

### 10. 处方表 (prescriptions)

记录医生为患者开具的处方信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表 |
| drug_list | TEXT | | 药品清单 |
| usage | TEXT | | 用法用量 |
| create_time | DATETIME | | 创建时间 |

### 11. 检查项目表 (checkups)

记录医生为患者开具的检查项目。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表 |
| type | VARCHAR | | 检查类型（如体温、血压、胸片等） |
| description | TEXT | | 检查描述 |
| result | TEXT | | 检查结果 |
| status | INT | | 状态（0-待检查，1-已完成） |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

### 12. 医嘱表 (advices)

记录医生给患者的医嘱信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| doctor_id | BIGINT | FK, NOT NULL | 外键，关联 doctors 表 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表 |
| content | TEXT | | 医嘱内容 |
| status | INT | | 状态（如 0-未读，1-已读） |
| create_time | DATETIME | | 创建时间 |
| update_time | DATETIME | | 更新时间 |

### 13. 账单表 (billing)

存储患者的缴费账单信息。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| user_id | BIGINT | FK, NOT NULL | 外键，关联 users 表 |
| type | VARCHAR | | 账单类型（如 '挂号费', '药费', '检查费', '治疗费'） |
| amount | DECIMAL | | 金额 |
| status | VARCHAR | | 账单状态（如 '未支付', '已支付'） |
| create_time | DATETIME | | 创建时间 |

## 表关系说明

### 核心关系

1. **users ↔ doctors**: 一对一关系
   - 每个医生对应一个用户账号
   - 通过 doctors.user_id 外键关联

2. **users ↔ nurses**: 一对一关系
   - 每个护士对应一个用户账号
   - 通过 nurses.user_id 外键关联

3. **users ↔ patients**: 一对一关系
   - 每个患者对应一个用户账号
   - 通过 patients.user_id 外键关联

4. **departments ↔ doctors**: 一对多关系
   - 一个科室可以有多个医生
   - 通过 doctors.department_id 外键关联

5. **doctors ↔ schedules**: 一对多关系
   - 一个医生可以有多个排班记录
   - 通过 schedules.doctor_id 外键关联

6. **patients ↔ registrations**: 一对多关系
   - 一个患者可以有多条挂号记录
   - 通过 registrations.patient_id 外键关联

7. **departments ↔ registrations**: 一对多关系
   - 一个科室可以有多条挂号记录
   - 通过 registrations.department_id 外键关联

8. **users/departments/doctors ↔ appointments**: 多对一关系
   - 一次预约关联一个用户、一个医生和一个科室
   - 通过 appointments 表的外键关联

9. **doctors/users ↔ medical_records/prescriptions/advices/checkups**: 一对多关系
   - 一个医生可以为多个患者创建就诊记录、处方、医嘱和检查项目
   - 通过对应表的外键关联

10. **users ↔ billing**: 一对多关系
    - 一个用户可以有多条账单记录
    - 通过 billing.user_id 外键关联

## 业务流程说明

### 预约就诊流程

1. 患者（users 表，role='patient'）创建预约 → **appointments** 表
2. 系统根据医生排班 → **schedules** 表更新剩余名额
3. 患者到院挂号 → **registrations** 表
4. 患者就诊时，医生创建：
   - 就诊记录 → **medical_records** 表
   - 处方 → **prescriptions** 表
   - 检查项目 → **checkups** 表
   - 医嘱 → **advices** 表
5. 护士协助诊疗：
   - 更新病历记录 → **medical_records** 表（体温、血压、护士记录）
6. 系统生成账单 → **billing** 表

### 数据流向

```
用户注册 → users 表
    ↓
患者信息 → patients 表
    ↓
预约挂号 → appointments 表 → schedules 表（更新剩余名额）
    ↓
现场挂号 → registrations 表
    ↓
医生接诊 → medical_records 表
    ↓
开具处方 → prescriptions 表
    ↓
开具检查 → checkups 表
    ↓
下达医嘱 → advices 表
    ↓
生成账单 → billing 表
    ↓
护士护理 → medical_records 表（更新体温、血压、护士记录）
```

## 设计特点

1. **统一用户管理**: 通过 users 表统一管理所有角色的账号，使用 role 字段区分（ADMIN、DOCTOR、NURSE、PATIENT）
2. **角色独立表**: doctors、nurses、patients 表分别存储各角色的详细信息，与用户账号分离
3. **完整就诊流程**: 从预约→挂号→就诊→处方→检查→医嘱→账单→护理，形成完整闭环
4. **外键约束**: 所有关联关系都使用外键约束，保证数据完整性
5. **时间记录**: 关键业务表都包含 create_time 和 update_time 字段，便于追踪和审计
6. **统一主键**: 所有实体类的主键统一使用 `private Long id`，遵循统一的命名规范
