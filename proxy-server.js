const http = require('http');
const { URL } = require('url');

const PORT = 8888; // 代理服务器端口

const server = http.createServer((req, res) => {
    const parsedUrl = new URL(req.url, `http://${req.headers.host}`);
    const pathname = parsedUrl.pathname;
    
    let targetHost;
    let targetPort;
    
    // 路由判断
    if (pathname.startsWith('/live/')) {
        // SRS 拉流 -> 转发到 8081
        targetHost = '127.0.0.1';
        targetPort = 8081;
        console.log(`[Proxy] ${req.method} ${pathname} -> ${targetHost}:${targetPort}`);
    } else if (pathname.startsWith('/api/')) {
        // 后端 API -> 转发到 8080
        targetHost = '127.0.0.1';
        targetPort = 8080;
        console.log(`[Proxy] ${req.method} ${pathname} -> ${targetHost}:${targetPort}`);
    } else {
        res.writeHead(404);
        res.end('Not Found');
        return;
    }
    
    // 创建代理请求
    const options = {
        hostname: targetHost,
        port: targetPort,
        path: parsedUrl.pathname + parsedUrl.search,
        method: req.method,
        headers: req.headers
    };
    
    const proxyReq = http.request(options, (proxyRes) => {
        res.writeHead(proxyRes.statusCode, proxyRes.headers);
        proxyRes.pipe(res);
    });
    
    proxyReq.on('error', (e) => {
        console.error(`[Proxy Error] ${e.message}`);
        res.writeHead(500);
        res.end('Proxy Error: ' + e.message);
    });
    
    req.pipe(proxyReq);
});

server.listen(PORT, () => {
    console.log(`==========================================`);
    console.log(`反向代理服务器已启动`);
    console.log(`监听端口：${PORT}`);
    console.log(``);
    console.log(`路由规则:`);
    console.log(`  /live/* -> http://127.0.0.1:8081 (SRS 拉流)`);
    console.log(`  /api/*  -> http://127.0.0.1:8080 (后端 API)`);
    console.log(`==========================================`);
});
