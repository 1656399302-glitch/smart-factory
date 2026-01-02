#!/bin/bash

# 修复数据库编码脚本
# 将数据库和所有表转换为utf8mb4编码

DB_NAME="ry-vue"
DB_USER="root"
DB_PASS="123456"

echo "========================================"
echo "  修复数据库编码为utf8mb4"
echo "========================================"
echo ""

# 检查MySQL连接
if ! mysql -u${DB_USER} -p${DB_PASS} -e "SELECT 1;" > /dev/null 2>&1; then
    echo "[错误] 无法连接到MySQL数据库"
    exit 1
fi

echo "[1/4] 修改数据库默认字符集..."
mysql -u${DB_USER} -p${DB_PASS} -e "ALTER DATABASE \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>&1 | grep -v "Warning"
echo "[完成] 数据库字符集已修改"
echo ""

echo "[2/4] 获取需要修改的表..."
# 获取所有表名
TABLES=$(mysql -u${DB_USER} -p${DB_PASS} -e "USE \`${DB_NAME}\`; SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = '${DB_NAME}' AND TABLE_TYPE = 'BASE TABLE';" 2>&1 | grep -v "Warning" | grep -v "TABLE_NAME" | grep -v "^$")

TABLE_COUNT=$(echo "$TABLES" | wc -l | tr -d ' ')
echo "[信息] 找到 $TABLE_COUNT 张表需要检查"
echo ""

echo "[3/4] 修改所有表的字符集..."
COUNT=0
for table in $TABLES; do
    mysql -u${DB_USER} -p${DB_PASS} ${DB_NAME} -e "ALTER TABLE \`${table}\` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>&1 | grep -v "Warning" | grep -E "(ERROR|Error)" || true
    COUNT=$((COUNT + 1))
    if [ $((COUNT % 10)) -eq 0 ]; then
        echo "  已处理 $COUNT/$TABLE_COUNT 张表..."
    fi
done
echo "[完成] 所有表字符集已修改"
echo ""

echo "[4/4] 验证修改结果..."
# 检查是否还有非utf8mb4的表
NON_UTF8MB4=$(mysql -u${DB_USER} -p${DB_PASS} -e "USE \`${DB_NAME}\`; SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = '${DB_NAME}' AND TABLE_COLLATION != 'utf8mb4_general_ci';" 2>&1 | grep -v "Warning" | tail -1)

if [ "$NON_UTF8MB4" = "0" ]; then
    echo "[成功] 所有表已转换为utf8mb4编码"
else
    echo "[警告] 仍有 $NON_UTF8MB4 张表未转换为utf8mb4编码"
fi

echo ""
echo "========================================"
echo "  编码修复完成！"
echo "========================================"

