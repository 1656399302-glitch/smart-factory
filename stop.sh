#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "========================================"
echo "  停止AR/VR数字孪生管理系统服务"
echo "========================================"
echo ""

# 停止后端服务
if [ -f "ruoyi-admin/backend.pid" ]; then
    BACKEND_PID=$(cat ruoyi-admin/backend.pid)
    if ps -p $BACKEND_PID > /dev/null 2>&1; then
        echo -e "${YELLOW}[提示]${NC} 正在停止后端服务 (PID: $BACKEND_PID)..."
        kill $BACKEND_PID
        sleep 3
        if ps -p $BACKEND_PID > /dev/null 2>&1; then
            echo -e "${YELLOW}[提示]${NC} 强制停止后端服务..."
            kill -9 $BACKEND_PID
        fi
        echo -e "${GREEN}[成功]${NC} 后端服务已停止"
        rm -f ruoyi-admin/backend.pid
    else
        echo -e "${YELLOW}[提示]${NC} 后端服务未运行"
        rm -f ruoyi-admin/backend.pid
    fi
else
    # 尝试通过进程名查找
    BACKEND_PID=$(ps aux | grep -E "ruoyi-admin.jar|spring-boot:run" | grep -v grep | awk '{print $2}' | head -1)
    if [ ! -z "$BACKEND_PID" ]; then
        echo -e "${YELLOW}[提示]${NC} 发现后端进程 (PID: $BACKEND_PID)，正在停止..."
        kill $BACKEND_PID
        sleep 3
        if ps -p $BACKEND_PID > /dev/null 2>&1; then
            kill -9 $BACKEND_PID
        fi
        echo -e "${GREEN}[成功]${NC} 后端服务已停止"
    else
        echo -e "${YELLOW}[提示]${NC} 未找到运行中的后端服务"
    fi
fi
echo ""

# 停止前端服务
if [ -f "frontend.pid" ]; then
    FRONTEND_PID=$(cat frontend.pid)
    if ps -p $FRONTEND_PID > /dev/null 2>&1; then
        echo -e "${YELLOW}[提示]${NC} 正在停止前端服务 (PID: $FRONTEND_PID)..."
        kill $FRONTEND_PID
        sleep 2
        if ps -p $FRONTEND_PID > /dev/null 2>&1; then
            echo -e "${YELLOW}[提示]${NC} 强制停止前端服务..."
            kill -9 $FRONTEND_PID
        fi
        echo -e "${GREEN}[成功]${NC} 前端服务已停止"
        rm -f frontend.pid
    else
        echo -e "${YELLOW}[提示]${NC} 前端服务未运行"
        rm -f frontend.pid
    fi
else
    # 尝试通过进程名查找
    FRONTEND_PID=$(ps aux | grep "npm run dev\|vue-cli-service serve" | grep -v grep | awk '{print $2}' | head -1)
    if [ ! -z "$FRONTEND_PID" ]; then
        echo -e "${YELLOW}[提示]${NC} 发现前端进程 (PID: $FRONTEND_PID)，正在停止..."
        kill $FRONTEND_PID
        sleep 2
        if ps -p $FRONTEND_PID > /dev/null 2>&1; then
            kill -9 $FRONTEND_PID
        fi
        echo -e "${GREEN}[成功]${NC} 前端服务已停止"
    else
        echo -e "${YELLOW}[提示]${NC} 未找到运行中的前端服务"
    fi
fi
echo ""

echo "========================================"
echo "  所有服务已停止"
echo "========================================"

