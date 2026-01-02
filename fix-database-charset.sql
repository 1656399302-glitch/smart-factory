-- 修复数据库编码为utf8mb4
-- 执行方法: mysql -uroot -p123456 < fix-database-charset.sql

USE `ry-vue`;

-- 1. 修改数据库默认字符集
ALTER DATABASE `ry-vue` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 2. 修改所有表的字符集
SET FOREIGN_KEY_CHECKS=0;

-- 获取所有表名并生成ALTER语句
SET @tables = NULL;
SELECT GROUP_CONCAT('`', table_name, '`') INTO @tables
FROM information_schema.tables
WHERE table_schema = 'ry-vue'
AND table_type = 'BASE TABLE';

SET @tables = CONCAT('ALTER TABLE ', @tables, ' CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci');

-- 如果表数量较多，可以分批执行
-- 这里使用动态SQL来执行
SET @sql = @tables;
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET FOREIGN_KEY_CHECKS=1;

-- 3. 验证修改结果
SELECT 
    TABLE_NAME,
    TABLE_COLLATION,
    CHARACTER_SET_NAME
FROM information_schema.TABLES t
JOIN information_schema.COLLATION_CHARACTER_SET_APPLICABILITY ccs
    ON t.TABLE_COLLATION = ccs.COLLATION_NAME
WHERE TABLE_SCHEMA = 'ry-vue'
ORDER BY TABLE_NAME;

