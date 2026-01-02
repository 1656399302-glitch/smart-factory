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

:: 判断使用Maven还是jar包启动
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    set USE_JAR=1
) else (
    set USE_JAR=0
)

:: 检查是否需要编译
set NEED_BUILD=0
if %USE_JAR%==1 (
    :: 使用jar包启动，检查jar包是否存在
    if not exist "ruoyi-admin\target\ruoyi-admin.jar" (
        set NEED_BUILD=1
    )
) else (
    :: 使用Maven启动，检查模块是否已编译
    if not exist "ruoyi-common\target\classes" (
        set NEED_BUILD=1
    )
    if not exist "ruoyi-system\target\classes" (
        set NEED_BUILD=1
    )
    if not exist "ruoyi-framework\target\classes" (
        set NEED_BUILD=1
    )
)

:: 如果需要编译，先编译项目
if %NEED_BUILD%==1 (
    echo ========================================
    echo   正在编译项目...
    echo ========================================
    echo.
    echo [提示] 首次启动需要编译项目，这可能需要几分钟...
    
    if %USE_JAR%==1 (
        echo [错误] 未找到jar包且Maven未安装，无法编译项目
        echo [提示] 请先安装Maven或手动执行 mvn clean package 打包
        pause
        exit /b 1
    ) else (
        :: 编译并安装所有模块到本地仓库
        mvn clean install -DskipTests > logs\build.log 2>&1
        if %errorlevel% neq 0 (
            echo [错误] 项目编译失败，请查看日志: logs\build.log
            pause
            exit /b 1
        )
        echo [成功] 项目编译完成
        echo.
    )
)

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
        echo [错误] 未找到jar包
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

echo [提示] 前端服务正在启动中，请查看前端服务窗口确认端口...
echo [提示] 等待前端服务启动（约20秒）...
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo   启动完成！
echo ========================================
echo.
echo 后端服务地址: http://localhost:8081
echo 前端服务地址: 请查看前端服务窗口中的实际端口（通常是 http://localhost:80）
echo.
echo 提示: 
echo   - 后端和前端服务已在独立窗口中运行
echo   - 关闭对应窗口即可停止服务
echo   - 如需停止所有服务，请关闭所有相关窗口
echo   - 前端日志会显示在启动窗口中
echo.
pause

