# 三维可视化模块（M1）功能说明

## 📋 模块概述

三维可视化模块（M1）是基于 RuoYi 框架开发的工厂三维场景可视化与编辑系统。该模块支持加载工厂三维模型，在场景中展示设备点位，并通过可视化编辑器对设备进行位置、旋转、缩放等变换操作。模块遵循 RuoYi 的分层架构规范，与现有业务逻辑解耦，仅读取 `factory` 和 `device` 表数据，不修改既有表结构。

## 🎯 核心功能

### 1. 三维总览（Viewer）

**功能描述：** 三维场景查看与编辑界面，支持工厂模型和设备模型的渲染展示。

**主要特性：**
- ✅ **工厂模型渲染**：加载并显示工厂的三维模型（支持 glb/gltf/fbx/obj 格式）
- ✅ **设备模型渲染**：自动加载已绑定设备的 3D 模型，按绑定位置、旋转、缩放参数渲染
- ✅ **设备状态可视化**：
  - 颜色映射：`running`（绿色）、`stopped`（灰色）、`alarm`（红色）、`offline`（深灰色）
  - 状态标签：在设备上方显示“正常使用”/“检修”等状态信息
- ✅ **可视化编辑（类 Unity 编辑器）**：
  - 编辑模式开关
  - 点击设备模型进行选择
  - Gizmo 操作：平移（W）、旋转（E）、缩放（R）
  - 实时预览变换效果
  - 保存选中设备的变换到数据库
- ✅ **场景导出**：导出场景 JSON 文件，包含工厂模型和设备绑定信息
- ✅ **设备统计**：显示设备总数、已绑定数量
- ✅ **未绑定设备显示**：可选择以圆形布局显示未绑定设备的点位标记

**使用流程：**
1. 选择工厂 → 点击“加载”按钮
2. 系统自动加载该工厂的发布态模型
3. 加载已绑定设备的模型并渲染到场景中
4. （可选）开启编辑模式，选择设备进行位置调整
5. 保存编辑结果，下次加载时按保存的位置渲染

**访问路径：** `工厂沙盘AR资源` → `三维可视化` → `三维总览`

---

### 2. 工厂模型管理（Factory Model）

**功能描述：** 管理工厂的三维模型文件，支持上传、编辑、发布等操作。

**主要功能：**
- ✅ **模型上传**：支持 glb、gltf、fbx、obj 格式（最大 50MB）
- ✅ **模型信息管理**：
  - 模型名称
  - 关联工厂（`factory_id`）
  - 模型格式
  - 默认相机参数（JSON）
- ✅ **状态管理**：
  - `draft`（草稿）：编辑中，不在 Viewer 中显示
  - `published`（已发布）：可在 Viewer 中加载
- ✅ **CRUD 操作**：新增、查询、修改、删除、导出

**使用场景：**
- 上传工厂的 3D 模型文件
- 设置默认相机视角
- 发布模型供 Viewer 使用

**访问路径：** `工厂沙盘AR资源` → `三维可视化` → `工厂模型管理`

---

### 3. 设备模型库（Device Model）

**功能描述：** 管理设备的三维模型资源库，供点位绑定时选择使用。

**主要功能：**
- ✅ **模型上传**：支持 glb、gltf、fbx、obj 格式（最大 50MB）
- ✅ **模型分类**：支持按分类组织设备模型
- ✅ **默认缩放**：设置模型的默认缩放比例
- ✅ **状态管理**：
  - `enabled`（启用）：可在绑定中选择
  - `disabled`（禁用）：不可选择
- ✅ **CRUD 操作**：新增、查询、修改、删除、导出

**使用场景：**
- 上传各类设备的 3D 模型文件
- 建立设备模型资源库
- 在点位绑定时选择对应的设备模型

**访问路径：** `工厂沙盘AR资源` → `三维可视化` → `设备模型库`

---

### 4. 点位绑定管理（Device Scene Binding）

**功能描述：** 管理设备在三维场景中的位置、旋转、缩放等绑定信息。

**主要功能：**
- ✅ **绑定创建**：
  - 选择工厂模型（`factory_model_id`）
  - 选择设备（`device_number`）
  - 选择设备模型（`device_model_id`）
  - 设置位置（position_x/y/z）
  - 设置旋转（rotation_x/y/z，单位：度）
  - 设置缩放（scale_x/y/z）
- ✅ **绑定编辑**：修改已绑定设备的位置、旋转、缩放参数
- ✅ **绑定删除**：解除设备与场景的绑定关系
- ✅ **CRUD 操作**：新增、查询、修改、删除、导出

**使用场景：**
- 在三维场景中为设备设置初始位置
- 通过表单输入坐标、旋转角度、缩放比例
- 或在 Viewer 的编辑模式中可视化调整后保存

**访问路径：** `工厂沙盘AR资源` → `三维可视化` → `点位绑定管理`

---

## 🗄️ 数据库表结构

### factory_model（工厂模型表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| factory_id | bigint | 关联 factory.factory_id |
| name | varchar(128) | 模型名称 |
| model_url | varchar(512) | 模型文件地址 |
| model_format | varchar(32) | 模型格式（glb/gltf/fbx/obj） |
| status | varchar(32) | 状态（draft/published） |
| default_camera | text | 默认相机参数（JSON） |
| remark | varchar(500) | 备注 |

### device_model（设备模型表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar(128) | 名称 |
| category | varchar(64) | 分类 |
| model_url | varchar(512) | 模型文件地址 |
| model_format | varchar(32) | 模型格式 |
| default_scale | varchar(255) | 默认缩放 |
| status | varchar(32) | 状态（enabled/disabled） |
| remark | varchar(500) | 备注 |

### device_scene_binding（设备点位绑定表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| factory_model_id | bigint | 关联 factory_model.id |
| device_number | bigint | 关联 device.device_number |
| device_model_id | bigint | 关联 device_model.id |
| position_x/y/z | decimal(18,6) | 位置坐标 |
| rotation_x/y/z | decimal(18,6) | 旋转角度（度） |
| scale_x/y/z | decimal(18,6) | 缩放比例 |

---

## 🔧 技术架构

### 后端（Spring Boot）
- **分层架构**：Controller → Service → Mapper → Domain
- **技术栈**：
  - Spring Boot + MyBatis
  - RuoYi 标准返回类型（`AjaxResult`、`TableDataInfo`）
  - RuoYi 权限控制（`@PreAuthorize`）
  - RuoYi 操作日志（`@Log`）
- **文件存储**：本地存储（`FileUploadUtils`）
- **API 端点**：
  - `GET /scene/viewer/{factoryId}`：获取 Viewer 聚合数据（工厂模型 + 设备列表 + 绑定信息）

### 前端（Vue2 + Element UI）
- **技术栈**：
  - Vue 2.x + Element UI
  - Three.js（3D 渲染引擎）
  - TransformControls（Gizmo 编辑器）
  - GLTFLoader、FBXLoader、OBJLoader（模型加载器）
- **组件结构**：
  - `ruoyi-ui/src/views/three/viewer/index.vue`：三维总览页面
  - `ruoyi-ui/src/views/three/factoryModel/index.vue`：工厂模型管理
  - `ruoyi-ui/src/views/three/deviceModel/index.vue`：设备模型库
  - `ruoyi-ui/src/views/three/binding/index.vue`：点位绑定管理
- **API 服务**：`ruoyi-ui/src/api/three/`

---

## 📦 安装与部署

### 1. 数据库初始化
执行 SQL 脚本创建表结构和菜单：
```bash
# 创建表结构
mysql -u用户名 -p数据库名 < sql/three_m1_tables.sql

# 创建菜单和权限
mysql -u用户名 -p数据库名 < sql/three_m1_menu.sql
```

### 2. 后端部署
模块已集成到 RuoYi 多模块项目中，无需额外配置。确保：
- `ruoyi-common` 模块已包含 `MimeTypeUtils` 对 3D 模型格式的支持
- `ruoyi-admin` 模块已包含 `three` 包下的 Controller、Service、Mapper

### 3. 前端部署
```bash
cd ruoyi-ui
npm install
npm run build:prod
```

### 4. 文件上传配置
确保 `application.yml` 中配置了文件上传路径：
```yaml
# 文件路径
file:
  path: D:/ruoyi/uploadPath
```

---

## 🎮 使用指南

### 快速开始

1. **上传工厂模型**
   - 进入 `工厂模型管理`
   - 点击“新增”，选择工厂，上传模型文件（glb/gltf/fbx/obj）
   - 设置默认相机参数（可选）
   - 点击“发布”按钮，将状态改为 `published`

2. **上传设备模型**
   - 进入 `设备模型库`
   - 点击“新增”，上传设备模型文件
   - 设置分类和默认缩放（可选）

3. **绑定设备到场景**
   - 进入 `点位绑定管理`
   - 点击“新增”，选择工厂模型、设备、设备模型
   - 输入位置坐标、旋转角度、缩放比例
   - 保存

4. **查看三维场景**
   - 进入 `三维总览`
   - 选择工厂，点击“加载”
   - 查看工厂模型和设备模型的渲染效果

5. **编辑设备位置（可视化）**
   - 在 `三维总览` 中开启“编辑模式”
   - 点击场景中的设备模型
   - 使用 Gizmo 进行平移（W）、旋转（E）、缩放（R）
   - 点击“保存选中”按钮，将变换保存到数据库

---

## 📝 注意事项

1. **模型格式支持**：目前支持 glb、gltf、fbx、obj 格式，建议优先使用 glb/gltf（体积小、加载快）
2. **文件大小限制**：单个模型文件最大 50MB
3. **设备状态映射**：
   - `running` → 绿色（#00ff00）
   - `stopped` → 灰色（#808080）
   - `alarm` → 红色（#ff0000）
   - `offline` → 深灰色（#36454F）
4. **编辑模式限制**：只能编辑已绑定且有设备模型的设备
5. **场景文件**：可通过“导出场景JSON”功能导出场景配置，用于备份或迁移

---

## 🔍 API 接口说明

### Viewer 聚合接口
```
GET /scene/viewer/{factoryId}
```
返回该工厂的发布态模型、设备列表（含绑定信息、状态颜色、设备模型 URL）的聚合数据。

**响应结构：**
```json
{
  "factoryModel": {
    "id": 1,
    "name": "工厂A模型",
    "modelUrl": "/upload/xxx.glb",
    "modelFormat": "glb",
    "defaultCamera": "{\"position\":[0,10,10]}"
  },
  "devices": [
    {
      "deviceNumber": 1001,
      "name": "设备1",
      "status": "running",
      "statusColor": "#00ff00",
      "bindingId": 1,
      "deviceModelId": 1,
      "deviceModelUrl": "/upload/device1.fbx",
      "deviceModelFormat": "fbx",
      "positionX": 0.0,
      "positionY": 0.0,
      "positionZ": 0.0,
      "rotationX": 0.0,
      "rotationY": 0.0,
      "rotationZ": 0.0,
      "scaleX": 1.0,
      "scaleY": 1.0,
      "scaleZ": 1.0
    }
  ]
}
```

---

## 📞 技术支持

如有问题或建议，请联系开发团队。

---

**版本：** M1（初始版本）  
**更新日期：** 2026-01-04

