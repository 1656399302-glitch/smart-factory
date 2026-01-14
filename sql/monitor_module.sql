-- =====================================================
-- 数据采集与监控模块 P0 SQL脚本
-- 创建时间: 2026-01-14
-- 说明: 包含device_sensor, sensor_data, alarm_rule, alarm_record 四张表
--      以及菜单配置
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 设备传感器表 device_sensor（配置表）
-- 描述某台 device 上有哪些传感器，以及传感器编码、类型、单位与启用状态
-- ----------------------------
DROP TABLE IF EXISTS `device_sensor`;
CREATE TABLE `device_sensor` (
  `sensor_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '传感器ID',
  `device_number` int(11) NOT NULL COMMENT '设备编号（关联device表）',
  `sensor_code` varchar(100) NOT NULL COMMENT '传感器编码（设备侧唯一）',
  `sensor_name` varchar(200) NOT NULL COMMENT '传感器名称',
  `sensor_type` varchar(50) DEFAULT 'custom' COMMENT '传感器类型（temperature/pressure/vibration/current/custom）',
  `unit` varchar(50) DEFAULT NULL COMMENT '单位（℃、MPa、mm/s、A 等）',
  `collect_interval` int(11) DEFAULT NULL COMMENT '采集周期（秒）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`sensor_id`),
  UNIQUE KEY `uk_device_sensor_code` (`device_number`, `sensor_code`),
  KEY `idx_device_number` (`device_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备传感器表';

-- ----------------------------
-- 2. 传感器数据表 sensor_data（时序数据表）
-- 存储传感器采集值与时间戳
-- ----------------------------
DROP TABLE IF EXISTS `sensor_data`;
CREATE TABLE `sensor_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sensor_id` bigint(20) NOT NULL COMMENT '传感器ID',
  `device_number` int(11) NOT NULL COMMENT '设备编号（冗余字段，便于按设备查）',
  `value` decimal(20,6) NOT NULL COMMENT '采集值',
  `data_time` datetime NOT NULL COMMENT '采集时间',
  `create_time` datetime DEFAULT NULL COMMENT '入库时间',
  PRIMARY KEY (`id`),
  KEY `idx_sensor_data_time` (`sensor_id`, `data_time`),
  KEY `idx_device_data_time` (`device_number`, `data_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器数据表';

-- ----------------------------
-- 3. 报警规则表 alarm_rule（阈值规则）
-- 定义当某传感器值超过/低于阈值时触发报警
-- ----------------------------
DROP TABLE IF EXISTS `alarm_rule`;
CREATE TABLE `alarm_rule` (
  `rule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(200) NOT NULL COMMENT '规则名称',
  `sensor_id` bigint(20) NOT NULL COMMENT '传感器ID',
  `compare_type` varchar(20) NOT NULL COMMENT '比较类型（gt/ge/lt/le/between）',
  `threshold_min` decimal(20,6) DEFAULT NULL COMMENT '阈值下限（用于between或下限判断）',
  `threshold_max` decimal(20,6) DEFAULT NULL COMMENT '阈值上限（用于between或上限判断）',
  `severity` varchar(20) DEFAULT 'medium' COMMENT '严重程度（low/medium/high）',
  `enabled` char(1) DEFAULT '1' COMMENT '是否启用（0停用 1启用）',
  `description` varchar(500) DEFAULT NULL COMMENT '规则描述',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`rule_id`),
  KEY `idx_sensor_id` (`sensor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警规则表';

-- ----------------------------
-- 4. 报警记录表 alarm_record（报警历史）
-- 记录报警触发历史与处理状态
-- ----------------------------
DROP TABLE IF EXISTS `alarm_record`;
CREATE TABLE `alarm_record` (
  `alarm_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '报警ID',
  `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
  `device_number` int(11) NOT NULL COMMENT '设备编号',
  `sensor_id` bigint(20) NOT NULL COMMENT '传感器ID',
  `alarm_time` datetime NOT NULL COMMENT '报警触发时间',
  `alarm_value` decimal(20,6) NOT NULL COMMENT '触发时的值',
  `severity` varchar(20) DEFAULT 'medium' COMMENT '严重程度（low/medium/high）',
  `status` varchar(20) DEFAULT 'new' COMMENT '状态（new/acknowledged/resolved）',
  `handled_by` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handled_time` datetime DEFAULT NULL COMMENT '处理时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`alarm_id`),
  KEY `idx_rule_sensor_status` (`rule_id`, `sensor_id`, `status`),
  KEY `idx_device_number` (`device_number`),
  KEY `idx_alarm_time` (`alarm_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警记录表';

-- ----------------------------
-- 插入测试数据
-- ----------------------------

-- 传感器测试数据（关联设备15-高效离心泵）
INSERT INTO `device_sensor` VALUES (1, 15, 'TEMP_001', '主轴温度传感器', 'temperature', '℃', 60, '0', '监测主轴运行温度', 'admin', NOW(), '', NULL);
INSERT INTO `device_sensor` VALUES (2, 15, 'PRES_001', '出口压力传感器', 'pressure', 'MPa', 30, '0', '监测泵出口压力', 'admin', NOW(), '', NULL);
INSERT INTO `device_sensor` VALUES (3, 15, 'VIBR_001', '振动传感器', 'vibration', 'mm/s', 10, '0', '监测设备振动', 'admin', NOW(), '', NULL);
INSERT INTO `device_sensor` VALUES (4, 16, 'TEMP_001', '控制柜温度', 'temperature', '℃', 120, '0', '监测控制柜温度', 'admin', NOW(), '', NULL);
INSERT INTO `device_sensor` VALUES (5, 16, 'CURR_001', '主电机电流', 'current', 'A', 5, '0', '监测主电机运行电流', 'admin', NOW(), '', NULL);

-- 报警规则测试数据
INSERT INTO `alarm_rule` VALUES (1, '主轴温度过高报警', 1, 'gt', NULL, 85.000000, 'high', '1', '主轴温度超过85℃触发高级别报警', 'admin', NOW(), '', NULL);
INSERT INTO `alarm_rule` VALUES (2, '主轴温度预警', 1, 'gt', NULL, 70.000000, 'medium', '1', '主轴温度超过70℃触发中级别预警', 'admin', NOW(), '', NULL);
INSERT INTO `alarm_rule` VALUES (3, '出口压力异常', 2, 'between', 0.200000, 1.500000, 'high', '1', '出口压力超出0.2-1.5MPa范围报警', 'admin', NOW(), '', NULL);
INSERT INTO `alarm_rule` VALUES (4, '振动过大报警', 3, 'gt', NULL, 10.000000, 'high', '1', '振动超过10mm/s触发报警', 'admin', NOW(), '', NULL);
INSERT INTO `alarm_rule` VALUES (5, '控制柜温度过高', 4, 'gt', NULL, 45.000000, 'medium', '1', '控制柜温度超过45℃预警', 'admin', NOW(), '', NULL);

-- 传感器数据测试（最近的采集数据）
INSERT INTO `sensor_data` VALUES (1, 1, 15, 65.500000, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (2, 1, 15, 66.200000, DATE_SUB(NOW(), INTERVAL 4 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (3, 1, 15, 67.800000, DATE_SUB(NOW(), INTERVAL 3 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (4, 1, 15, 68.500000, DATE_SUB(NOW(), INTERVAL 2 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (5, 1, 15, 69.200000, DATE_SUB(NOW(), INTERVAL 1 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (6, 2, 15, 0.850000, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (7, 2, 15, 0.860000, DATE_SUB(NOW(), INTERVAL 4 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (8, 2, 15, 0.870000, DATE_SUB(NOW(), INTERVAL 3 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (9, 3, 15, 4.500000, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (10, 3, 15, 4.800000, DATE_SUB(NOW(), INTERVAL 4 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (11, 4, 16, 38.500000, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NOW());
INSERT INTO `sensor_data` VALUES (12, 5, 16, 15.200000, DATE_SUB(NOW(), INTERVAL 5 MINUTE), NOW());

-- ----------------------------
-- 菜单配置
-- 放置在"预测性维护"同级下方
-- ----------------------------

-- 数据采集与监控 一级菜单 (menu_id 从 2700 开始避免冲突)
INSERT INTO `sys_menu` VALUES (2700, '数据采集与监控', 0, 5, 'datamonitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, '数据采集与监控模块');

-- 传感器管理菜单
INSERT INTO `sys_menu` VALUES (2701, '传感器管理', 2700, 1, 'sensor', 'monitor/sensor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:sensor:list', 'component', 'admin', NOW(), '', NULL, '传感器管理菜单');
INSERT INTO `sys_menu` VALUES (2702, '传感器查询', 2701, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:sensor:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2703, '传感器新增', 2701, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:sensor:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2704, '传感器修改', 2701, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:sensor:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2705, '传感器删除', 2701, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:sensor:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2706, '传感器导出', 2701, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:sensor:export', '#', 'admin', NOW(), '', NULL, '');

-- 实时数据菜单（最新值列表）
INSERT INTO `sys_menu` VALUES (2710, '实时数据', 2700, 2, 'realtime', 'monitor/realtime/index', '', '', 1, 0, 'C', '0', '0', 'monitor:data:view', 'dashboard', 'admin', NOW(), '', NULL, '实时数据菜单');

-- 历史数据菜单
INSERT INTO `sys_menu` VALUES (2715, '历史数据', 2700, 3, 'history', 'monitor/history/index', '', '', 1, 0, 'C', '0', '0', 'monitor:data:view', 'chart', 'admin', NOW(), '', NULL, '历史数据查询菜单');

-- 报警规则菜单
INSERT INTO `sys_menu` VALUES (2720, '报警规则', 2700, 4, 'rule', 'monitor/rule/index', '', '', 1, 0, 'C', '0', '0', 'monitor:rule:list', 'edit', 'admin', NOW(), '', NULL, '报警规则管理菜单');
INSERT INTO `sys_menu` VALUES (2721, '规则查询', 2720, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:rule:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2722, '规则新增', 2720, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:rule:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2723, '规则修改', 2720, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:rule:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2724, '规则删除', 2720, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:rule:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2725, '规则导出', 2720, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:rule:export', '#', 'admin', NOW(), '', NULL, '');

-- 报警记录菜单
INSERT INTO `sys_menu` VALUES (2730, '报警记录', 2700, 5, 'alarm', 'monitor/alarm/index', '', '', 1, 0, 'C', '0', '0', 'monitor:alarm:list', 'message', 'admin', NOW(), '', NULL, '报警记录菜单');
INSERT INTO `sys_menu` VALUES (2731, '报警查询', 2730, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:alarm:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2732, '报警确认', 2730, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:alarm:handle', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2733, '报警关闭', 2730, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:alarm:handle', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` VALUES (2734, '报警导出', 2730, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'monitor:alarm:export', '#', 'admin', NOW(), '', NULL, '');

-- 采集测试菜单
INSERT INTO `sys_menu` VALUES (2740, '采集测试', 2700, 6, 'collect', 'monitor/collect/index', '', '', 1, 0, 'C', '0', '0', 'monitor:data:collect', 'form', 'admin', NOW(), '', NULL, '本地采集测试菜单');

SET FOREIGN_KEY_CHECKS = 1;
