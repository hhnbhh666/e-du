@echo off
chcp 65001 >nul
echo ========================================
echo Starting proxy server...
echo ========================================
start "Proxy Server" node proxy-server.js

timeout /t 2 /nobreak >nul

echo.
echo ========================================
echo Starting natapp...
echo ========================================
start "natapp" natapp.exe -config=config.ini

echo.
echo ========================================
echo All services started!
echo ========================================
echo.
echo Services:
echo   - Proxy: http://localhost:8888
echo   - SRS: http://localhost:8081
echo   - Backend API: http://localhost:8080
echo   - RTMP: rtmp://localhost:1935
echo.
echo Check natapp console for public URL
echo ========================================
pause
