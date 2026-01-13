-- 预测性维护模块（P0）- 数据表与菜单
-- 说明：本脚本创建 prediction_result 表，并添加"预测性维护"菜单

-- 强制会话编码，避免插入中文出现乱码
SET NAMES utf8mb4;

-- =============================================
-- 一、创建 prediction_result 表
-- =============================================

DROP TABLE IF EXISTS `prediction_result`;
CREATE TABLE `prediction_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `factory_id` bigint DEFAULT NULL COMMENT '工厂ID',
  `asset_type` varchar(50) NOT NULL COMMENT '资产类型：equipment/device',
  `asset_id` bigint NOT NULL COMMENT '资产ID',
  `prediction_time` datetime NOT NULL COMMENT '预测生成时间',
  `risk_level` varchar(20) NOT NULL COMMENT '风险等级：low/medium/high',
  `risk_score` int NOT NULL DEFAULT 0 COMMENT '风险分数（0-100）',
  `reason_summary` varchar(500) DEFAULT NULL COMMENT '原因摘要',
  `reason_detail_json` text COMMENT '原因详情JSON（可解释）',
  `recommendation` text COMMENT '建议维护动作',
  `status` varchar(20) NOT NULL DEFAULT 'new' COMMENT '状态：new/acknowledged/ignored/resolved',
  `handled_by` varchar(100) DEFAULT NULL COMMENT '处理人',
  `handled_time` datetime DEFAULT NULL COMMENT '处理时间',
  `model_version` varchar(50) DEFAULT 'RULE_P0_v1' COMMENT '模型版本',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_asset` (`asset_type`, `asset_id`),
  KEY `idx_factory` (`factory_id`),
  KEY `idx_prediction_time` (`prediction_time`),
  KEY `idx_status` (`status`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预测结果表';

-- =============================================
-- 二、添加菜单与权限
-- =============================================

-- 获取"运维记录"菜单的parent_id，将"预测性维护"放在同级下一个位置
SET @maintenance_parent_id = (SELECT parent_id FROM sys_menu WHERE component='ar/maintenance/index' LIMIT 1);
SET @maintenance_order_num = (SELECT order_num FROM sys_menu WHERE component='ar/maintenance/index' LIMIT 1);
SET @predict_order_num = IFNULL(@maintenance_order_num, 0) + 1;

-- 先删除旧的预测性维护菜单（如果存在）
DROP TEMPORARY TABLE IF EXISTS tmp_predict_dir;
CREATE TEMPORARY TABLE tmp_predict_dir AS
SELECT menu_id, order_num
FROM sys_menu
WHERE parent_id = @maintenance_parent_id
  AND path = 'predict'
  AND menu_type = 'M';

-- 清理 sys_role_menu 关联
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
LEFT JOIN tmp_predict_dir t ON (m.menu_id = t.menu_id OR m.parent_id = t.menu_id)
WHERE m.perms LIKE 'predict:%'
   OR m.component LIKE 'predict/%'
   OR t.menu_id IS NOT NULL;

-- 清理菜单本身
DELETE m
FROM sys_menu m
LEFT JOIN tmp_predict_dir t ON (m.menu_id = t.menu_id OR m.parent_id = t.menu_id)
WHERE m.perms LIKE 'predict:%'
   OR m.component LIKE 'predict/%'
   OR t.menu_id IS NOT NULL;

-- 为插入腾位置：同级菜单从 @predict_order_num 起整体后移一位
UPDATE sys_menu
SET order_num = order_num + 1
WHERE parent_id = @maintenance_parent_id
  AND order_num >= @predict_order_num;

-- 目录：预测性维护
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('预测性维护',@maintenance_parent_id,@predict_order_num,'predict',NULL,'','',1,0,'M','0','0','', 'tool','admin',NOW(),'预测性维护目录（P0）');
SET @predict_dir_id = LAST_INSERT_ID();


-- 菜单：预测结果
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('预测结果',@predict_dir_id,1,'result','predict/result/index',NULL,'',1,0,'C','0','0','predict:result:list','list','admin',NOW(),'预测结果列表');
SET @predict_result_id = LAST_INSERT_ID();

-- 预测结果按钮权限
INSERT INTO `sys_menu` (`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
('查询',@predict_result_id,1,'','','','',1,0,'F','0','0','predict:result:query','#','admin',NOW(),''),
('查看详情',@predict_result_id,2,'','','','',1,0,'F','0','0','predict:result:view','#','admin',NOW(),''),
('确认处理',@predict_result_id,3,'','','','',1,0,'F','0','0','predict:result:handle','#','admin',NOW(),''),
('删除',@predict_result_id,4,'','','','',1,0,'F','0','0','predict:result:remove','#','admin',NOW(),''),
('导出',@predict_result_id,5,'','','','',1,0,'F','0','0','predict:result:export','#','admin',NOW(),'');

-- 菜单：手动预测
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('手动预测',@predict_dir_id,2,'task','predict/task/index',NULL,'',1,0,'C','0','0','predict:task:list','job','admin',NOW(),'手动触发预测');
SET @predict_task_id = LAST_INSERT_ID();

-- 手动预测按钮权限
INSERT INTO `sys_menu` (`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
('运行预测',@predict_task_id,1,'','','','',1,0,'F','0','0','predict:task:run','#','admin',NOW(),'');

-- 将菜单权限赋予管理员角色（role_id=1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu
WHERE menu_id IN (@predict_dir_id, @predict_result_id, @predict_task_id)
   OR parent_id IN (@predict_dir_id, @predict_result_id, @predict_task_id);

