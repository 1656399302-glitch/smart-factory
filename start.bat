@echo off
chcp 65001 >nul
title AR/VR数字孪生管理系统 - 一键启动

echo ========================================
echo   AR/VR数字孪生管理系统 - 一键启动
echo ========================================
echo.

:: 获取脚本所在目录
set SCRIPT_DIR=%~dp0
cd /d %SCRIPT_DIR%

:: 创建日志目录
if not exist "logs" mkdir logs
if not exist "ruoyi-admin\logs" mkdir ruoyi-admin\logs

:: 检查Java环境
echo [1/4] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java环境，请先安装JDK 1.8或更高版本
    pause
    exit /b 1
)
echo [成功] Java环境检测通过
echo.

:: 检查Maven环境
echo [2/4] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未检测到Maven环境，将尝试使用jar包启动
    set USE_JAR=1
) else (
    echo [成功] Maven环境检测通过
    set USE_JAR=0
)
echo.

:: 检查Node.js环境
echo [3/4] 检查Node.js环境...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js环境，请先安装Node.js
    pause
    exit /b 1
)
echo [成功] Node.js环境检测通过
echo.

:: 检查npm环境
npm -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到npm环境，请先安装npm
    pause
    exit /b 1
)
echo [成功] npm环境检测通过
echo.

:: 检查前端依赖
echo [4/4] 检查前端依赖...
cd ruoyi-ui
if not exist "node_modules" (
    echo [提示] 前端依赖未安装，正在安装...
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 前端依赖安装失败
        pause
        exit /b 1
    )
    echo [成功] 前端依赖安装完成
) else (
    echo [成功] 前端依赖已存在
)
cd ..
echo.

:: 启动后端
echo ========================================
echo   正在启动后端服务...
echo ========================================
echo.

if %USE_JAR%==1 (
    :: 使用jar包启动
    if exist "ruoyi-admin\target\ruoyi-admin.jar" (
        echo [提示] 使用jar包启动后端...
        cd ruoyi-admin\target
        start "后端服务" cmd /k "java -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -jar ruoyi-admin.jar"
        cd ..\..
    ) else (
        echo [错误] 未找到jar包，请先执行 mvn clean package 打包
        pause
        exit /b 1
    )
) else (
    :: 使用Maven启动
    echo [提示] 使用Maven启动后端...
    cd ruoyi-admin
    start "后端服务" cmd /k "mvn spring-boot:run"
    cd ..
)

:: 等待后端启动
echo [提示] 等待后端服务启动（约30秒）...
timeout /t 30 /nobreak >nul

:: 检查后端是否启动成功
echo [提示] 检查后端服务状态...
curl -s http://localhost:8081 >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 后端服务可能未完全启动，请稍后手动检查
) else (
    echo [成功] 后端服务启动成功！访问地址: http://localhost:8081
)
echo.

:: 启动前端
echo ========================================
echo   正在启动前端服务...
echo ========================================
echo.

cd ruoyi-ui
start "前端服务" cmd /k "npm run dev"
cd ..

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo 后端服务地址: http://localhost:8081
echo 前端服务地址: http://localhost:80 (默认端口，请查看前端控制台)
echo.
echo 提示: 
echo   - 后端和前端服务已在独立窗口中运行
echo   - 关闭对应窗口即可停止服务
echo   - 如需停止所有服务，请关闭所有相关窗口
echo.
pause

