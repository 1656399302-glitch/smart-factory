#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 创建日志目录
mkdir -p logs ruoyi-admin/logs

echo "========================================"
echo "  AR/VR数字孪生管理系统 - 一键启动"
echo "========================================"
echo ""

# 判断使用Maven还是jar包启动
USE_JAR=0
if ! command -v mvn &> /dev/null; then
    USE_JAR=1
fi

# 是否强制重新编译（可通过环境变量或参数触发）
# 用法：
#   FORCE_BUILD=1 ./start.sh
#   ./start.sh --build
FORCE_BUILD=0
if [ "$FORCE_BUILD" = "1" ]; then
    FORCE_BUILD=1
fi
for arg in "$@"; do
    if [ "$arg" = "--build" ]; then
        FORCE_BUILD=1
    fi
done

# 检查是否需要编译
NEED_BUILD=0
if [ $USE_JAR -eq 1 ]; then
    # 使用jar包启动，检查jar包是否存在
    if [ ! -f "ruoyi-admin/target/ruoyi-admin.jar" ]; then
        NEED_BUILD=1
    fi
else
    # 使用Maven启动，检查模块是否已编译
    if [ ! -d "ruoyi-common/target/classes" ] || [ ! -d "ruoyi-system/target/classes" ] || [ ! -d "ruoyi-framework/target/classes" ]; then
        NEED_BUILD=1
    fi
fi

# 强制编译（用于代码有改动但 target/classes 仍存在的情况）
if [ $FORCE_BUILD -eq 1 ]; then
    NEED_BUILD=1
fi

# 如果需要编译，先编译项目
if [ $NEED_BUILD -eq 1 ]; then
    echo "========================================"
    echo "  正在编译项目..."
    echo "========================================"
    echo ""
    echo -e "${YELLOW}[提示]${NC} 首次启动需要编译项目，这可能需要几分钟..."
    
    if [ $USE_JAR -eq 1 ]; then
        echo -e "${RED}[错误]${NC} 未找到jar包且Maven未安装，无法编译项目"
        echo -e "${YELLOW}[提示]${NC} 请先安装Maven或手动执行 mvn clean package 打包"
        exit 1
    else
        # 编译并安装所有模块到本地仓库
        mvn clean install -DskipTests > logs/build.log 2>&1
        if [ $? -ne 0 ]; then
            echo -e "${RED}[错误]${NC} 项目编译失败，请查看日志: logs/build.log"
            exit 1
        fi
        echo -e "${GREEN}[成功]${NC} 项目编译完成"
        echo ""
    fi
fi

# 启动后端
echo "========================================"
echo "  正在启动后端服务..."
echo "========================================"
echo ""

if [ $USE_JAR -eq 1 ]; then
    # 使用jar包启动
    if [ -f "ruoyi-admin/target/ruoyi-admin.jar" ]; then
        echo -e "${YELLOW}[提示]${NC} 使用jar包启动后端..."
        cd ruoyi-admin/target
        nohup java -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -jar ruoyi-admin.jar > ../logs/backend.log 2>&1 &
        BACKEND_PID=$!
        echo $BACKEND_PID > ../backend.pid
        cd ../..
        echo -e "${GREEN}[成功]${NC} 后端服务已启动，PID: $BACKEND_PID"
    else
        echo -e "${RED}[错误]${NC} 未找到jar包"
        exit 1
    fi
else
    # 使用Maven启动
    echo -e "${YELLOW}[提示]${NC} 使用Maven启动后端..."
    cd ruoyi-admin
    nohup mvn spring-boot:run > logs/backend.log 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > backend.pid
    cd ..
    echo -e "${GREEN}[成功]${NC} 后端服务已启动，PID: $BACKEND_PID"
fi

# 等待后端启动
echo -e "${YELLOW}[提示]${NC} 等待后端服务启动（约30秒）..."
sleep 30

# 检查后端是否启动成功
echo -e "${YELLOW}[提示]${NC} 检查后端服务状态..."
if curl -s http://localhost:8081 > /dev/null 2>&1; then
    echo -e "${GREEN}[成功]${NC} 后端服务启动成功！访问地址: http://localhost:8081"
else
    echo -e "${YELLOW}[警告]${NC} 后端服务可能未完全启动，请稍后手动检查"
    echo -e "${YELLOW}[提示]${NC} 查看日志: tail -f ruoyi-admin/logs/backend.log"
fi
echo ""

# 启动前端
echo "========================================"
echo "  正在启动前端服务..."
echo "========================================"
echo ""

cd ruoyi-ui
# 启动前端服务
npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../frontend.pid
cd ..

echo -e "${YELLOW}[提示]${NC} 等待前端服务启动（约20秒）..."
sleep 20

# 从日志中提取前端实际端口
FRONTEND_PORT=$(grep -oP "Local:\s+http://localhost:\K\d+" logs/frontend.log 2>/dev/null | tail -1)
if [ -z "$FRONTEND_PORT" ]; then
    # 如果无法从日志提取，尝试从进程获取
    FRONTEND_PORT=$(lsof -ti:80 2>/dev/null | head -1)
    if [ -z "$FRONTEND_PORT" ]; then
        FRONTEND_PORT="80"
    fi
fi

# 检查前端是否启动成功
if ps -p $FRONTEND_PID > /dev/null 2>&1; then
    echo -e "${GREEN}[成功]${NC} 前端服务已启动，PID: $FRONTEND_PID"
    if [ -n "$FRONTEND_PORT" ] && [ "$FRONTEND_PORT" != "80" ]; then
        echo -e "${YELLOW}[提示]${NC} 前端服务运行在端口: $FRONTEND_PORT (80端口需要root权限)"
    fi
else
    echo -e "${YELLOW}[警告]${NC} 前端服务可能未完全启动，请查看日志"
    echo -e "${YELLOW}[提示]${NC} 查看日志: tail -f logs/frontend.log"
fi
echo ""

echo "========================================"
echo "  启动完成！"
echo "========================================"
echo ""
echo "后端服务地址: http://localhost:8081"
if [ -n "$FRONTEND_PORT" ]; then
    echo "前端服务地址: http://localhost:1024"
else
    echo "前端服务地址: http://localhost:1024 (请查看前端日志确认实际端口)"
fi
echo ""
echo "提示: "
echo "  - 后端PID: $BACKEND_PID (保存在 ruoyi-admin/backend.pid)"
echo "  - 前端PID: $FRONTEND_PID (保存在 frontend.pid)"
echo "  - 后端日志: ruoyi-admin/logs/backend.log"
echo "  - 前端日志: logs/frontend.log"
echo "  - 停止服务: ./stop.sh 或 kill $BACKEND_PID $FRONTEND_PID"
echo ""

