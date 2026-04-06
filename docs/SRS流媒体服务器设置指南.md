# SRS 流媒体服务器设置指南

## 一、什么是 SRS？

SRS (Simple Realtime Server) 是一个简单高效的实时视频服务器，支持 RTMP、WebRTC、HLS、HTTP-FLV 等协议，非常适合用于直播场景。

## 二、下载和安装 SRS

### 方式一：使用预编译版本（推荐 Windows 用户）

1. **下载 SRS**：
   - 访问 GitHub 发布页面：https://github.com/ossrs/srs/releases
   - 下载最新的 Windows 版本（例如 `srs-win64.zip`）
   - 或者从官网下载：https://ossrs.net/srs/releases.html

2. **解压文件**：
   - 将下载的压缩包解压到一个目录，例如 `C:\srs`

3. **创建配置文件**：
   在 SRS 目录下创建 `conf\live.conf` 文件，内容如下：
   ```nginx
   listen              1935;
   max_connections     1000;
   srs_log_tank        file;
   srs_log_file        ./objs/srs.log;
   daemon              off;
   
   http_api {
       enabled         on;
       listen          1985;
   }
   
   http_server {
       enabled         on;
       listen          8081;
       dir             ./objs/nginx/html;
   }
   
   rtc_server {
       enabled         off;
       listen          8000;
       udp_port        8000;
   }
   
   vhost __defaultVhost__ {
       hls {
           enabled         on;
           hls_path        ./objs/nginx/html;
           hls_fragment    10;
           hls_window      60;
       }
       
       http_remux {
           enabled         on;
           mount           [vhost]/[app]/[stream].flv;
       }
   }
   ```

### 方式二：使用 Docker（推荐）

如果你有 Docker，这是最简单的方式：

```bash
docker run -p 1935:1935 -p 1985:1985 -p 8081:8080 ossrs/srs:5
```

## 三、启动 SRS 服务器

### Windows 方式：

1. 打开命令提示符（CMD）或 PowerShell
2. 进入 SRS 目录：
   ```bash
   cd C:\srs
   ```
3. 启动服务器：
   ```bash
   objs\srs.exe -c conf\live.conf
   ```

### Docker 方式：

```bash
docker run -p 1935:1935 -p 1985:1985 -p 8081:8080 ossrs/srs:5
```

启动成功后，你会看到类似这样的输出：
```
[2024-04-02 10:00:00.000] [INFO] [12345] [srs] SRS started
[2024-04-02 10:00:00.000] [INFO] [12345] [srs] listen at tcp://0.0.0.0:1935, fd=3
[2024-04-02 10:00:00.000] [INFO] [12345] [srs] listen at tcp://0.0.0.0:8081, fd=4
```

## 四、使用 OBS 推流

### 1. 下载 OBS Studio
- 访问：https://obsproject.com/
- 下载并安装 OBS Studio

### 2. 配置推流

1. 打开 OBS Studio
2. 点击**设置** → **推流**
3. 选择服务类型为**自定义**
4. 服务器填写：`rtmp://localhost/live`
5. 串流密钥填写：你的直播间 ID（例如 `room123`）
6. 点击**确定**

### 3. 开始推流

1. 在 OBS 中添加视频源（例如摄像头、显示器捕获等）
2. 点击右下角的**开始推流**按钮

## 五、观看直播

### 1. 在应用中观看

1. 打开你的应用
2. 创建一个直播间或进入一个已有的直播间
3. 你应该能看到 OBS 推流的内容

### 2. 直接访问 HTTP-FLV 地址

你也可以直接在浏览器中使用支持 FLV 的播放器（如 VLC 或 PotPlayer）打开：
```
http://localhost:8081/live/你的直播间ID.flv
```

## 六、端口说明

| 端口 | 协议 | 用途 |
|------|------|------|
| 1935 | RTMP | 推流 |
| 8081 | HTTP | HTTP-FLV 拉流、HLS |
| 1985 | HTTP | API 接口 |
| 8080 | HTTP | Spring Boot 后端服务（原后端端口） |

## 七、常见问题

### Q: 无法连接到 SRS 服务器？
A: 检查防火墙是否阻止了 1935 和 8080 端口。

### Q: 视频无法播放？
A: 确保：
1. SRS 服务器正在运行
2. OBS 正在推流
3. 推流地址和拉流地址中的 stream key 一致

### Q: 如何在手机上观看？
A: 你需要：
1. 确保手机和电脑在同一个局域网
2. 将地址中的 `localhost` 改为电脑的局域网 IP（例如 `192.168.1.100`）
3. 确保防火墙允许局域网访问

## 八、下一步

配置好 SRS 后，你可以：
1. 尝试不同的视频源（摄像头、屏幕、媒体文件）
2. 添加音频设备
3. 调整视频质量（分辨率、码率）
4. 测试多人同时观看

祝你使用愉快！
