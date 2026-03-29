# 医院挂号系统

基于Spring Boot开发的医院挂号管理系统，支持患者、医生、护士、管理员四种角色的功能。

## 技术栈

- **后端框架**: Spring Boot 3.2.0
- **数据库**: MySQL
- **ORM框架**: Spring Data JPA
- **模板引擎**: Thymeleaf
- **安全框架**: Spring Security
- **构建工具**: Maven
- **Java版本**: 17+

## 功能模块

### 1. 患者端
- 预约挂号：选择科室、医生、时间段进行预约
- 号源查询：查看各科室号源信息
- 科室医生查询：浏览科室和医生信息
- 我的订单：查看预约记录和就诊历史
- 个人信息管理：修改个人资料

### 2. 医生端
- 号源发布：设置可预约时间和号源数量
- 患者信息查看：查看预约患者详情
- 诊疗工作：开具处方、检查申请
- 医嘱管理：查看和修改医嘱

### 3. 护士端
- 患者信息查看：浏览患者基本信息和病历
- 协助诊疗：记录患者体征数据
- 缴费管理：处理患者缴费事宜

### 4. 管理员端
- 用户管理：添加、删除、修改用户
- 权限管理：设置用户角色和权限
- 数据管理：数据备份和恢复
- 系统设置：配置系统参数

## 数据库设计

### 主要表结构
- **users**: 用户表（患者、医生、护士、管理员）
- **departments**: 科室表
- **doctors**: 医生信息表
- **appointments**: 预约挂号表
- **medical_records**: 病历表
- **prescriptions**: 处方表
- **billing**: 收费表

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
在 `src/main/resources/application.properties` 中配置数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. 运行项目
```bash
mvn clean install
mvn spring-boot:run
```

### 4. 访问系统
- 系统地址: http://localhost:8080
- 默认管理员账号: admin / admin123
- 默认医生账号: doctor1 / doctor123
- 默认护士账号: nurse1 / nurse123
- 默认患者账号: patient1 / patient123

## 项目结构

```
hospital-system/
├── src/
│   ├── main/
│   │   ├── java/com/hospital/
│   │   │   ├── config/           # 配置类
│   │   │   ├── controller/       # 控制器
│   │   │   ├── service/          # 服务层
│   │   │   ├── repository/       # 数据访问层
│   │   │   ├── entity/           # 实体类
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── security/        # 安全配置
│   │   │   └── Application.java  # 应用入口
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf模板
│   │       │   ├── patient/      # 患者端页面
│   │       │   ├── doctor/       # 医生端页面
│   │       │   ├── nurse/       # 护士端页面
│   │       │   ├── admin/       # 管理员端页面
│   │       │   └── *.html       # 公共页面
│   │       └── static/          # 静态资源
│   │           └── css/         # 样式文件
│   └── test/                   # 测试代码
└── pom.xml                     # Maven配置
```

## 使用说明

### 患者使用流程
1. 注册/登录系统
2. 浏览科室和医生信息
3. 选择科室和医生进行预约
4. 填写预约信息并提交
5. 查看预约记录和状态

### 医生使用流程
1. 登录系统
2. 查看今日预约患者
3. 查看患者详细信息
4. 进行诊疗并记录病历
5. 开具处方和医嘱
6. 更新排班信息

### 护士使用流程
1. 登录系统
2. 查看患者信息
3. 协助医生进行诊疗
4. 处理患者缴费
5. 查看患者病历和处方

### 管理员使用流程
1. 登录系统
2. 管理用户账号
3. 管理科室信息
4. 管理医生信息
5. 查看系统统计数据

## 安全配置

系统使用Spring Security进行权限管理，不同角色具有不同的访问权限：
- **PATIENT**: 访问患者端功能
- **DOCTOR**: 访问医生端功能
- **NURSE**: 访问护士端功能
- **ADMIN**: 访问管理员端功能

## 开发说明

### 添加新功能
1. 在 `entity` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `service` 包中创建业务逻辑类
4. 在 `controller` 包中创建控制器
5. 在 `templates` 中创建前端页面

### 数据库迁移
系统使用JPA的自动建表功能，启动时会自动创建表结构。

## 注意事项

1. 首次启动会自动创建默认用户和科室数据
2. 密码使用BCrypt加密存储
3. 所有页面都需要登录才能访问
4. 系统支持角色权限控制

## 许可证

本项目仅供学习和教学使用。
