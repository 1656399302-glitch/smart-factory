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

# 检查Java环境
echo -e "${BLUE}[1/4]${NC} 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}[错误]${NC} 未检测到Java环境，请先安装JDK 1.8或更高版本"
    exit 1
fi
echo -e "${GREEN}[成功]${NC} Java环境检测通过"
echo ""

# 检查Maven环境
echo -e "${BLUE}[2/4]${NC} 检查Maven环境..."
USE_JAR=0
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}[警告]${NC} 未检测到Maven环境，将尝试使用jar包启动"
    USE_JAR=1
else
    echo -e "${GREEN}[成功]${NC} Maven环境检测通过"
fi
echo ""

# 检查Node.js环境
echo -e "${BLUE}[3/4]${NC} 检查Node.js环境..."
if ! command -v node &> /dev/null; then
    echo -e "${RED}[错误]${NC} 未检测到Node.js环境，请先安装Node.js"
    exit 1
fi
echo -e "${GREEN}[成功]${NC} Node.js环境检测通过"
echo ""

# 检查npm环境
if ! command -v npm &> /dev/null; then
    echo -e "${RED}[错误]${NC} 未检测到npm环境，请先安装npm"
    exit 1
fi
echo -e "${GREEN}[成功]${NC} npm环境检测通过"
echo ""

# 检查前端依赖
echo -e "${BLUE}[4/4]${NC} 检查前端依赖..."
cd ruoyi-ui
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}[提示]${NC} 前端依赖未安装，正在安装..."
    npm install
    if [ $? -ne 0 ]; then
        echo -e "${RED}[错误]${NC} 前端依赖安装失败"
        exit 1
    fi
    echo -e "${GREEN}[成功]${NC} 前端依赖安装完成"
else
    echo -e "${GREEN}[成功]${NC} 前端依赖已存在"
fi
cd ..
echo ""

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
        echo -e "${RED}[错误]${NC} 未找到jar包，请先执行 mvn clean package 打包"
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
nohup npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../frontend.pid
cd ..

echo -e "${GREEN}[成功]${NC} 前端服务已启动，PID: $FRONTEND_PID"
echo ""

echo "========================================"
echo "  启动完成！"
echo "========================================"
echo ""
echo "后端服务地址: http://localhost:8081"
echo "前端服务地址: http://localhost:80 (默认端口，请查看前端控制台)"
echo ""
echo "提示: "
echo "  - 后端PID: $BACKEND_PID (保存在 ruoyi-admin/backend.pid)"
echo "  - 前端PID: $FRONTEND_PID (保存在 frontend.pid)"
echo "  - 后端日志: ruoyi-admin/logs/backend.log"
echo "  - 前端日志: logs/frontend.log"
echo "  - 停止服务: ./stop.sh 或 kill $BACKEND_PID $FRONTEND_PID"
echo ""

