#!/bin/bash

# SQL导入脚本
# 用于导入所有SQL文件到MySQL数据库

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 数据库配置
DB_NAME="ry-vue"
DB_USER="root"
DB_PASS="123456"

echo "========================================"
echo "  MySQL数据库SQL导入脚本"
echo "========================================"
echo ""

# 检查MySQL是否运行
if ! mysql -u${DB_USER} -p${DB_PASS} -e "SELECT 1;" > /dev/null 2>&1; then
    echo -e "${RED}[错误]${NC} 无法连接到MySQL数据库"
    echo -e "${YELLOW}[提示]${NC} 请确保MySQL服务已启动: brew services start mysql"
    exit 1
fi

echo -e "${GREEN}[成功]${NC} MySQL连接正常"
echo ""

# 创建数据库
echo "========================================"
echo "  1. 创建数据库..."
echo "========================================"
mysql -u${DB_USER} -p${DB_PASS} -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>&1 | grep -v "Warning"
echo -e "${GREEN}[完成]${NC} 数据库创建完成"
echo ""

# 导入 ry-vue-all.sql
echo "========================================"
echo "  2. 导入 ry-vue-all.sql (基础数据库结构)..."
echo "========================================"
if mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} < sql/ry-vue-all.sql 2>&1 | grep -v "Warning" | grep -E "(ERROR|Error)"; then
    echo -e "${RED}[错误]${NC} ry-vue-all.sql 导入失败"
    exit 1
fi
echo -e "${GREEN}[完成]${NC} ry-vue-all.sql 导入完成"
echo ""

# 导入 ry_20240629.sql
echo "========================================"
echo "  3. 导入 ry_20240629.sql (数据库更新)..."
echo "========================================"
if mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} < sql/ry_20240629.sql 2>&1 | grep -v "Warning" | grep -E "(ERROR|Error)"; then
    echo -e "${YELLOW}[警告]${NC} ry_20240629.sql 导入时可能有错误，但继续执行"
fi
echo -e "${GREEN}[完成]${NC} ry_20240629.sql 导入完成"
echo ""

# 导入 quartz.sql
echo "========================================"
echo "  4. 导入 quartz.sql (定时任务表)..."
echo "========================================"
if mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} < sql/quartz.sql 2>&1 | grep -v "Warning" | grep -E "(ERROR|Error)"; then
    echo -e "${YELLOW}[警告]${NC} quartz.sql 导入时可能有错误，但继续执行"
fi
echo -e "${GREEN}[完成]${NC} quartz.sql 导入完成"
echo ""

# 导入 arvr.sql (跳过quartz表相关错误)
echo "========================================"
echo "  5. 导入 arvr.sql (AR/VR业务数据)..."
echo "========================================"
# 先临时禁用外键检查，导入后再启用
mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} -e "SET FOREIGN_KEY_CHECKS=0;" 2>&1 | grep -v "Warning"
mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} < sql/arvr.sql 2>&1 | grep -v "Warning" | grep -E "(ERROR|Error)" || true
mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} -e "SET FOREIGN_KEY_CHECKS=1;" 2>&1 | grep -v "Warning"
echo -e "${GREEN}[完成]${NC} arvr.sql 导入完成（已忽略quartz表重复定义错误）"
echo ""

# 验证导入结果
echo "========================================"
echo "  验证导入结果..."
echo "========================================"
TABLE_COUNT=$(mysql -u${DB_USER} -p${DB_PASS} -e "USE \`${DB_NAME}\`; SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '${DB_NAME}';" 2>&1 | grep -v "Warning" | tail -1)
echo -e "${GREEN}[成功]${NC} 数据库中共有 ${TABLE_COUNT} 张表"

# 显示部分表名
echo ""
echo "部分表列表:"
mysql -u${DB_USER} -p${DB_PASS} -e "USE \`${DB_NAME}\`; SHOW TABLES;" 2>&1 | grep -v "Warning" | head -20

echo ""
echo "========================================"
echo "  SQL导入完成！"
echo "========================================"
echo ""
echo "数据库名称: ${DB_NAME}"
echo "数据库用户: ${DB_USER}"
echo "表总数: ${TABLE_COUNT}"
echo ""
echo "现在可以启动后端服务了: ./start.sh"

