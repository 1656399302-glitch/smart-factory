# AR/VR数字孪生管理系统

<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">AR/VR数字孪生管理系统</h1>
<h4 align="center">基于RuoYi框架的AR/VR数字孪生管理系统</h4>

## 📋 项目简介

本项目是基于若依(RuoYi)框架开发的AR/VR数字孪生管理系统，主要用于管理工厂沙盘AR资源、学生作品VR应用以及校企合作应用。系统采用前后端分离架构，实现了完整的权限管理、资源管理和业务管理功能。

## 🚀 快速开始

### 一键启动（推荐）

**Windows系统**：
```bash
start.bat
```

**Linux/Mac系统**：
```bash
chmod +x start.sh stop.sh
./start.sh
```

详细说明请查看 [启动说明.md](./启动说明.md)

### 手动启动

#### 后端启动
```bash
cd ruoyi-admin
mvn spring-boot:run
# 或使用jar包
java -jar target/ruoyi-admin.jar
```

#### 前端启动
```bash
cd ruoyi-ui
npm install
npm run dev
```

## 🛠️ 技术栈

### 后端技术
- **框架**：Spring Boot 2.5.15
- **安全框架**：Spring Security 5.7.12
- **持久层**：MyBatis + PageHelper
- **数据库**：MySQL 8.0
- **缓存**：Redis
- **认证方式**：JWT Token
- **构建工具**：Maven

### 前端技术
- **框架**：Vue 2.x
- **UI组件**：Element UI
- **路由**：Vue Router
- **状态管理**：Vuex
- **HTTP客户端**：Axios

### 其他
- **文件存储**：阿里云OSS
- **定时任务**：Quartz
- **代码生成**：Velocity模板引擎

## 📦 项目结构

```
ruoyi/
├── ruoyi-admin/          # 主模块，启动类所在
│   ├── src/main/java/com/ruoyi/
│   │   ├── ar/           # AR模块
│   │   ├── vr/           # VR模块
│   │   ├── school/       # School模块
│   │   └── web/          # Web控制器
│   └── src/main/resources/
│       ├── mapper/       # MyBatis映射文件
│       └── application.yml
├── ruoyi-common/         # 通用工具模块
├── ruoyi-framework/      # 框架核心模块
├── ruoyi-system/         # 系统管理模块
├── ruoyi-quartz/         # 定时任务模块
├── ruoyi-generator/      # 代码生成模块
├── ruoyi-ui/             # 前端项目
└── sql/                  # 数据库脚本
```

## 🎯 核心功能

### 1. 工厂沙盘AR资源管理
- AR内容管理（视频/图文/音频）
- 二维码管理
- 工厂信息管理
- 设备管理
- 沙盘分区管理
- 库区管理
- 材料/产品管理
- 出入库管理
- 能耗统计
- 运维记录

### 2. 学生作品VR应用管理
- VR展厅管理
- VR展区管理
- 载体管理
- VR内容管理
- 作品管理

### 3. 校企合作应用管理
- 合作案例管理
- VR场景管理
- VR素材管理

### 4. 系统管理
- 用户管理
- 角色权限管理
- 菜单管理
- 部门管理
- 字典管理
- 参数配置
- 日志管理
- 定时任务
- 代码生成

## 📊 数据库设计

- **系统表**：约20张（用户、角色、菜单、日志等）
- **AR模块表**：约20张（工厂、设备、内容、仓储、交易等）
- **VR模块表**：约8张（展厅、展区、场景、作品等）
- **School模块表**：约5张（合作案例、课程等）
- **总计**：约64张表

详细数据库设计请查看SQL文件：
- `sql/arvr.sql` - AR/VR业务表
- `sql/ry-vue-all.sql` - 完整数据库脚本
- `sql/ry_20240629.sql` - 系统基础表

## 🔧 环境要求

- **JDK**：1.8 或更高版本
- **Maven**：3.6+（可选，用于编译）
- **Node.js**：14.x 或更高版本
- **npm**：随Node.js一起安装
- **MySQL**：8.0
- **Redis**：最新版本

## 📝 配置说明

### 数据库配置
编辑 `ruoyi-admin/src/main/resources/application-druid.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password
```

### Redis配置
编辑 `ruoyi-admin/src/main/resources/application.yml`：
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
```

### 文件存储配置
编辑 `ruoyi-admin/src/main/resources/application.yml`：
```yaml
dromara:
  x-file-storage:
    default-platform: aliyun-oss-1
    aliyun-oss:
      - platform: aliyun-oss-1
        access-key: your_access_key
        secret-key: your_secret_key
        end-point: oss-cn-beijing.aliyuncs.com
        bucket-name: your_bucket_name
```

## 🚀 部署说明

### 后端部署
```bash
# 打包
mvn clean package

# 运行jar包
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

### 前端部署
```bash
cd ruoyi-ui
npm run build:prod
# 将dist目录部署到nginx或其他web服务器
```

## 📖 文档

- [启动说明.md](./启动说明.md) - 一键启动脚本使用说明
- [Git仓库初始化指南.md](./Git仓库初始化指南.md) - Git仓库管理指南

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m '【需求】Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 提交规范

提交信息格式：`【类型】描述`

- **【需求】**：新功能
- **【调整】**：代码调整、重构
- **【修复】**：Bug修复

## 📞 联系方式

如有问题，请提交Issue或联系开发团队。

## 📜 许可证

本项目基于 [MIT License](./LICENSE) 开源。

---

**基于若依框架 v3.8.9 开发**
