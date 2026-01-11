-- M1 三维可视化模块（three）- 菜单与权限（sys_menu / sys_role_menu）
-- 说明：sys_menu.menu_id 为自增，脚本使用 LAST_INSERT_ID() 关联父子菜单

-- 强制会话编码，避免插入中文出现乱码
SET NAMES utf8mb4;

-- 入口挂载位置：
-- 放在“工厂沙盘AR资源”目录下，并位于“工厂（ar/factory/index）”同级的下一个位置
SET @ar_parent_id = (SELECT parent_id FROM sys_menu WHERE component='ar/factory/index' LIMIT 1);

-- 如果脚本重复执行：先删除旧的 three 菜单（含乱码目录）并回收同级 order_num，避免越跑越乱
DROP TEMPORARY TABLE IF EXISTS tmp_three_dir;
CREATE TEMPORARY TABLE tmp_three_dir AS
SELECT menu_id, order_num
FROM sys_menu
WHERE parent_id = @ar_parent_id
  AND path = 'three'
  AND menu_type = 'M';

-- 清理 sys_role_menu 关联（通过 perms 前缀/组件路径/目录菜单定位）
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON rm.menu_id = m.menu_id
LEFT JOIN tmp_three_dir t ON (m.menu_id = t.menu_id OR m.parent_id = t.menu_id)
WHERE m.perms LIKE 'three:%'
   OR m.component LIKE 'three/%'
   OR t.menu_id IS NOT NULL;

-- 清理菜单本身
DELETE m
FROM sys_menu m
LEFT JOIN tmp_three_dir t ON (m.menu_id = t.menu_id OR m.parent_id = t.menu_id)
WHERE m.perms LIKE 'three:%'
   OR m.component LIKE 'three/%'
   OR t.menu_id IS NOT NULL;

-- 回收同级 order_num：把删掉的目录占位补回去（支持多次/多条删除）
UPDATE sys_menu s
SET s.order_num = s.order_num - (
    SELECT COUNT(*) FROM tmp_three_dir d WHERE d.order_num < s.order_num
)
WHERE s.parent_id = @ar_parent_id;

-- 重新计算目标插入位置（回收 order_num 后）
SET @factory_order_num = (SELECT order_num FROM sys_menu WHERE component='ar/factory/index' LIMIT 1);
SET @three_order_num = IFNULL(@factory_order_num, 0) + 1;

-- 为插入腾位置：同级菜单从 @three_order_num 起整体后移一位
UPDATE sys_menu
SET order_num = order_num + 1
WHERE parent_id = @ar_parent_id
  AND order_num >= @three_order_num;

-- 目录：三维可视化（挂到 AR 资源目录下）
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('三维可视化',@ar_parent_id,@three_order_num,'three',NULL,'','',1,0,'M','0','0','', 'chart','admin',NOW(),'三维可视化目录（M1）');
SET @three_dir_id = LAST_INSERT_ID();

-- 菜单：三维总览 Viewer
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('三维总览',@three_dir_id,1,'viewer','three/viewer/index',NULL,'',1,0,'C','0','0','three:viewer:view','eye','admin',NOW(),'M1 Viewer');
SET @three_viewer_id = LAST_INSERT_ID();

-- 菜单：工厂模型管理
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('工厂模型管理',@three_dir_id,2,'factoryModel','three/factoryModel/index',NULL,'',1,0,'C','0','0','three:factoryModel:list','build','admin',NOW(),'M1 工厂模型管理');
SET @three_factory_model_id = LAST_INSERT_ID();

INSERT INTO `sys_menu` (`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
('查询',@three_factory_model_id,1,'','','','',1,0,'F','0','0','three:factoryModel:query','#','admin',NOW(),''),
('新增',@three_factory_model_id,2,'','','','',1,0,'F','0','0','three:factoryModel:add','#','admin',NOW(),''),
('修改',@three_factory_model_id,3,'','','','',1,0,'F','0','0','three:factoryModel:edit','#','admin',NOW(),''),
('删除',@three_factory_model_id,4,'','','','',1,0,'F','0','0','three:factoryModel:remove','#','admin',NOW(),''),
('发布',@three_factory_model_id,5,'','','','',1,0,'F','0','0','three:factoryModel:publish','#','admin',NOW(),'');

-- 菜单：设备模型库
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('设备模型库',@three_dir_id,3,'deviceModel','three/deviceModel/index',NULL,'',1,0,'C','0','0','three:deviceModel:list','documentation','admin',NOW(),'M1 设备模型库');
SET @three_device_model_id = LAST_INSERT_ID();

INSERT INTO `sys_menu` (`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
('查询',@three_device_model_id,1,'','','','',1,0,'F','0','0','three:deviceModel:query','#','admin',NOW(),''),
('新增',@three_device_model_id,2,'','','','',1,0,'F','0','0','three:deviceModel:add','#','admin',NOW(),''),
('修改',@three_device_model_id,3,'','','','',1,0,'F','0','0','three:deviceModel:edit','#','admin',NOW(),''),
('删除',@three_device_model_id,4,'','','','',1,0,'F','0','0','three:deviceModel:remove','#','admin',NOW(),'');

-- 菜单：点位绑定管理
INSERT INTO `sys_menu`
(`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`)
VALUES
('点位绑定管理',@three_dir_id,4,'binding','three/binding/index',NULL,'',1,0,'C','0','0','three:binding:list','cascader','admin',NOW(),'M1 点位绑定');
SET @three_binding_id = LAST_INSERT_ID();

INSERT INTO `sys_menu` (`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
('查询',@three_binding_id,1,'','','','',1,0,'F','0','0','three:binding:query','#','admin',NOW(),''),
('新增',@three_binding_id,2,'','','','',1,0,'F','0','0','three:binding:add','#','admin',NOW(),''),
('修改',@three_binding_id,3,'','','','',1,0,'F','0','0','three:binding:edit','#','admin',NOW(),''),
('删除',@three_binding_id,4,'','','','',1,0,'F','0','0','three:binding:remove','#','admin',NOW(),'');

-- 可选：将以上菜单权限直接赋予管理员角色（role_id=1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu
WHERE menu_id IN (@three_dir_id, @three_viewer_id, @three_factory_model_id, @three_device_model_id, @three_binding_id)
   OR parent_id IN (@three_dir_id, @three_factory_model_id, @three_device_model_id, @three_binding_id);


