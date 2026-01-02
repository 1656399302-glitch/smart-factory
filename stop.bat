@echo off
chcp 65001 >nul
title AR/VR数字孪生管理系统 - 停止服务

echo ========================================
echo   停止AR/VR数字孪生管理系统服务
echo ========================================
echo.

:: 停止后端服务
echo [提示] 正在查找后端服务进程...
for /f "tokens=2" %%a in ('jps -l ^| findstr ruoyi-admin') do (
    set BACKEND_PID=%%a
    echo [提示] 发现后端进程 PID: %%a，正在停止...
    taskkill /F /PID %%a >nul 2>&1
    if %errorlevel% equ 0 (
        echo [成功] 后端服务已停止
    ) else (
        echo [警告] 停止后端服务失败
    )
)

if not defined BACKEND_PID (
    echo [提示] 未找到运行中的后端服务
)
echo.

:: 停止前端服务
echo [提示] 正在查找前端服务进程...
for /f "tokens=2" %%a in ('tasklist /FI "WINDOWTITLE eq 前端服务*" /FO CSV /NH 2^>nul ^| findstr /C:"node.exe"') do (
    echo [提示] 发现前端进程，正在停止...
    taskkill /F /FI "WINDOWTITLE eq 前端服务*" >nul 2>&1
    if %errorlevel% equ 0 (
        echo [成功] 前端服务已停止
    )
)

:: 通过窗口标题停止
taskkill /F /FI "WINDOWTITLE eq 前端服务*" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq 后端服务*" >nul 2>&1

echo.
echo ========================================
echo   所有服务已停止
echo ========================================
echo.
pause

