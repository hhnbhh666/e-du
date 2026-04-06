@echo off
chcp 65001 >nul
echo ====================================
echo   ngrok 内网穿透启动脚本
echo ====================================
echo.

REM 检查 ngrok 是否存在
if not exist "%~dp0ngrok.exe" (
    echo [错误] ngrok.exe 不存在！
    echo 已自动下载到当前目录，请检查是否解压成功
    echo.
    pause
    exit /b 1
)

echo 正在启动 ngrok...
echo.
echo 隧道 1: 后端 API (8080 端口) - 用于小程序/APP 访问后端接口
echo 隧道 2: SRS 拉流 (8081 端口) - 用于小程序/APP 观看直播
echo.
echo [重要] 启动后请记录两个公网地址（类似：xxxx.ngrok.io）
echo 稍后需要更新到代码中
echo.
echo ====================================
echo.

REM 使用配置文件启动
start "ngrok" ngrok start --config ngrok.yml --all

timeout /t 3 /nobreak >nul

echo.
echo ngrok 已启动！
echo 公网地址将在 ngrok 窗口中显示
echo.
echo [提示] 如果是首次使用，需要先配置 authtoken:
echo 1. 访问 https://dashboard.ngrok.com/signup 注册
echo 2. 复制 Authtoken
echo 3. 在当前目录打开命令行，运行：ngrok config add-authtoken 你的 token
echo.
pause
