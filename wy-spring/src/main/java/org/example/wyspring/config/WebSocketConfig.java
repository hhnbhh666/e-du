package org.example.wyspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.example.wyspring.websocket.LiveDanmakuHandler;
import org.example.wyspring.websocket.LiveUserSocketHandler;

/**
 * WebSocket配置
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveDanmakuHandler danmakuHandler;
    private final LiveUserSocketHandler userSocketHandler;

    public WebSocketConfig(LiveDanmakuHandler danmakuHandler, LiveUserSocketHandler userSocketHandler) {
        this.danmakuHandler = danmakuHandler;
        this.userSocketHandler = userSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(danmakuHandler, "/ws/danmaku/{roomId}")
                .setAllowedOrigins("*");

        registry.addHandler(userSocketHandler, "/ws/live/{roomId}")
                .setAllowedOrigins("*");
    }
}