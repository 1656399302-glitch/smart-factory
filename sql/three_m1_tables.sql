-- M1 三维可视化模块（three）- 新增表结构
-- 说明：仅新增自身表；不修改 factory/device 等既有表结构

DROP TABLE IF EXISTS `factory_model`;
CREATE TABLE `factory_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `factory_id` bigint(20) NOT NULL COMMENT '关联 factory.factory_id',
  `name` varchar(128) NOT NULL COMMENT '模型名称',
  `model_url` varchar(512) NOT NULL COMMENT '模型文件地址（glb/gltf）',
  `model_format` varchar(32) DEFAULT NULL COMMENT '模型格式',
  `status` varchar(32) NOT NULL DEFAULT 'draft' COMMENT '状态（draft/published）',
  `default_camera` text DEFAULT NULL COMMENT '默认相机（JSON）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_factory_id` (`factory_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工厂模型表（M1）';

DROP TABLE IF EXISTS `device_model`;
CREATE TABLE `device_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `category` varchar(64) DEFAULT NULL COMMENT '分类（可选）',
  `model_url` varchar(512) NOT NULL COMMENT '模型文件地址',
  `model_format` varchar(32) DEFAULT NULL COMMENT '模型格式',
  `default_scale` varchar(255) DEFAULT NULL COMMENT '默认缩放（数值或JSON）',
  `status` varchar(32) NOT NULL DEFAULT 'enabled' COMMENT '状态（enabled/disabled）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备模型表（M1）';

DROP TABLE IF EXISTS `device_scene_binding`;
CREATE TABLE `device_scene_binding` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `factory_model_id` bigint(20) NOT NULL COMMENT '关联 factory_model.id',
  `device_number` bigint(20) NOT NULL COMMENT '关联 device.device_number',
  `device_model_id` bigint(20) DEFAULT NULL COMMENT '关联 device_model.id',
  `position_x` decimal(18,6) DEFAULT NULL COMMENT '位置X',
  `position_y` decimal(18,6) DEFAULT NULL COMMENT '位置Y',
  `position_z` decimal(18,6) DEFAULT NULL COMMENT '位置Z',
  `rotation_x` decimal(18,6) DEFAULT NULL COMMENT '旋转X',
  `rotation_y` decimal(18,6) DEFAULT NULL COMMENT '旋转Y',
  `rotation_z` decimal(18,6) DEFAULT NULL COMMENT '旋转Z',
  `scale_x` decimal(18,6) DEFAULT NULL COMMENT '缩放X',
  `scale_y` decimal(18,6) DEFAULT NULL COMMENT '缩放Y',
  `scale_z` decimal(18,6) DEFAULT NULL COMMENT '缩放Z',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_factory_model_device` (`factory_model_id`,`device_number`) USING BTREE,
  KEY `idx_factory_model_id` (`factory_model_id`) USING BTREE,
  KEY `idx_device_number` (`device_number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备点位绑定表（M1）';


