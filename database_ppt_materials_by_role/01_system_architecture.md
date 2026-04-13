# 医院管理系统数据库设计 - 系统架构总览

## 1. 系统概述

医院管理系统是一个综合性的医疗信息管理平台，旨在实现医院各部门的信息整合、流程优化和服务提升。数据库设计作为系统的核心基础，支撑着整个医院的业务运作。

## 2. 数据库设计目标

- **数据完整性**: 确保数据的准确性和一致性
- **系统性能**: 优化查询和存储效率
- **可扩展性**: 支持未来业务增长和功能扩展
- **安全性**: 保护敏感医疗数据
- **可维护性**: 便于系统维护和故障排查

## 3. 整体架构

### 3.1 数据库表关系图

```
+----------------+       +----------------+       +----------------+
|     users      |       | departments    |       |    doctors     |
+----------------+       +----------------+       +----------------+
| PK id          |<----->| PK id          |<----->| PK id          |
|    name        |       |    name        |       | FK user_id     |
|    age         |       |    description |       | FK department_ |
|    gender      |       |    location    |       |    id          |
|    phone       |       +----------------+       |    title       |
|    email       |       ^                        |    specialty   |
|    password    |       |                        |    schedule    |
|    role        |       |                        +----------------+
+----------------+       |                              ^
      ^                  |                              |
      |                  |                              |
      |                  |                              |
+----------------+       |       +----------------+     |
|  appointments  |       |       |    nurses      |     |
+----------------+       |       +----------------+     |
| PK id          |       |       | PK id          |     |
| FK patient_id  |<------+       | FK user_id     |     |
| FK doctor_id   |               | FK department_ |     |
| FK department_ |               |    id          |     |
|    appoint_time|               |    ward        |     |
|    status      |               |    title       |     |
|    symptoms    |               +----------------+
|    create_time |
+----------------+
      ^
      |
      |
+----------------+       +----------------+       +----------------+
|    patients    |       |   schedules    |       |medical_records |
+----------------+       +----------------+       +----------------+
| PK id          |<----->| PK id          |       | PK id          |
| FK user_id     |       | FK doctor_id   |       | FK doctor_id   |
|    medical_    |       |    start_time  |       | FK patient_id  |
|    record_num  |       |    end_time    |       | FK appointment_|
|    allergies   |       |    total_num   |       |    chief_compl |
|    emergency_  |       |    remain_num  |       |    present_ill |
|    contact     |       |    status      |       |    diagnosis   |
|    emergency_  |       +----------------+       |    diag_time   |
|    phone       |                                |    create_time |
+----------------+                                |    temperature |
                                                  |    blood_press |
                                                  |    nurse_notes |
                                                  +----------------+
      ^                                                 ^
      |                                                 |
      |                                                 |
+----------------+       +----------------+       +----------------+
| prescriptions  |       |    advices     |       |    billing     |
+----------------+       +----------------+       +----------------+
| PK id          |       | PK id          |       | PK id          |
| FK doctor_id   |       | FK doctor_id   |       | FK patient_id  |
| FK patient_id  |       | FK patient_id  |       | FK appointment_|
| FK appointment_|       | FK appointment_|       |    type        |
|    drug_list   |       |    content     |       |    amount      |
|    usage       |       |    status      |       |    status      |
|    create_time |       |    create_time |       |    create_time |
+----------------+       |    update_time |       +----------------+
                         +----------------+
                              ^
                              |
+----------------+            |
|    checkups    |            |
+----------------+
| PK id          |
| FK doctor_id   |<-----------+
| FK patient_id  |
| FK appointment_|
|    type        |
|    description |
|    result      |
|    status      |
|    create_time |
|    update_time |
+----------------+

+----------------+       +----------------+
|     drugs      |       | checkup_items  |
+----------------+       +----------------+
| PK id          |       | PK id          |
|    name        |       |    name        |
|    price       |       |    price       |
|    unit        |       |    description |
+----------------+       +----------------+

+----------------+
|    backups     |
+----------------+
| PK id          |
|    backup_type |
|    file_path   |
|    file_size   |
|    description |
|    backup_time |
|    status      |
|    remarks     |
+----------------+
```

### 3.2 角色模块划分

| 角色 | 主要功能 | 涉及表 |
|------|---------|--------|
| 管理员 | 系统管理、用户管理、科室管理、数据备份 | users, departments, doctors, nurses, patients, backups |
| 医生 | 患者诊疗、处方开具、检查申请、医嘱下达 | users, doctors, appointments, medical_records, prescriptions, checkups, advices, drugs, checkup_items |
| 护士 | 患者护理、病历记录更新、医嘱执行 | users, nurses, medical_records, advices, checkups |
| 患者 | 预约挂号、查看个人病历、缴费 | users, patients, appointments, medical_records, prescriptions, checkups, advices, billing |

## 4. 设计特点

1. **统一用户管理**: 通过 users 表统一管理所有角色的账号，使用 role 字段区分不同角色
2. **角色独立表**: 各角色（医生、护士、患者）拥有独立的详细信息表
3. **完整就诊流程**: 从预约到就诊、处方、检查、医嘱、账单形成完整闭环
4. **外键约束**: 所有关联关系都使用外键约束，保证数据完整性
5. **时间记录**: 关键业务表都包含时间戳字段，便于追踪和审计
6. **统一主键**: 所有表的主键统一使用自增 BIGINT 类型
7. **药品和检查项目管理**: 独立的表管理药品和检查项目信息
8. **备份管理**: 专门的备份记录表，确保数据安全

## 5. 数据流向

```
用户注册 → users 表
    ↓
患者信息 → patients 表
    ↓
预约挂号 → appointments 表 → schedules 表（更新剩余名额）
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
    ↓
数据库备份 → backups 表
```

## 6. 技术选型

- **数据库类型**: 关系型数据库（如 MySQL、PostgreSQL）
- **存储引擎**: InnoDB（支持事务和外键）
- **字符集**: UTF-8（支持中文）
- **索引策略**: 为频繁查询的字段创建索引
- **分区策略**: 对大表考虑使用分区技术提高性能