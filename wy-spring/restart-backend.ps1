# 结束占用 8080 的进程并启动 wy-spring（开发用）
$ErrorActionPreference = 'SilentlyContinue'
$lines = netstat -ano | Select-String ':8080\s+.*LISTENING'
foreach ($line in $lines) {
	$pid = ($line.Line -split '\s+')[-1]
	if ($pid -match '^\d+$') {
		Stop-Process -Id ([int]$pid) -Force
	}
}
Start-Sleep -Seconds 2
Set-Location $PSScriptRoot
mvn spring-boot:run
