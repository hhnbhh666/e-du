package org.example.wyspring.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.wyspring.dto.request.DanmakuSendRequest;
import org.example.wyspring.service.LiveService;
import org.example.wyspring.utils.JwtUtils;
import org.example.wyspring.vo.DanmakuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 直播弹幕WebSocket处理器
 */
@Slf4j
@Component
public class LiveDanmakuHandler extends TextWebSocketHandler {

    private final Map<Long, CopyOnWriteArraySet<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    @Autowired
    private LiveService liveService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();
        Long roomId = extractRoomId(uri);

        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);

        String token = getTokenFromSession(session);
        if (token != null) {
            Long userId = jwtUtils.getUserId(token);
            if (userId != null) {
                sessionUserMap.put(session.getId(), userId);
            }
        }

        log.info("[WS_DANMAKU_CONNECT] roomId={}, sessionId={}", roomId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject json = JSON.parseObject(payload);
        String type = json.getString("type");

        Long roomId = extractRoomId(session.getUri().toString());
        Long userId = sessionUserMap.get(session.getId());

        switch (type) {
            case "danmaku":
                handleDanmaku(session, roomId, userId, json);
                break;
            case "heartbeat":
                handleHeartbeat(session);
                break;
            default:
                break;
        }
    }

    private void handleDanmaku(WebSocketSession session, Long roomId, Long userId, JSONObject json) {
        if (userId == null) {
            sendError(session, "请先登录");
            return;
        }

        String content = json.getString("content");
        String color = json.getString("color");
        Integer danmakuType = json.getInteger("danmakuType");

        if (content == null || content.trim().isEmpty()) {
            sendError(session, "弹幕内容不能为空");
            return;
        }

        DanmakuSendRequest request = new DanmakuSendRequest();
        request.setContent(content);
        request.setColor(color != null ? color : "#FFFFFF");
        request.setType(danmakuType != null ? danmakuType : 1);

        try {
            DanmakuVO danmaku = liveService.sendDanmaku(userId, roomId, request);
            JSONObject broadcastMsg = new JSONObject();
            broadcastMsg.put("type", "danmaku");
            broadcastMsg.put("data", danmaku);

            broadcastToRoom(roomId, broadcastMsg.toJSONString());
        } catch (Exception e) {
            log.error("[WS_DANMAKU_ERROR] {}", e.getMessage());
            sendError(session, e.getMessage());
        }
    }

    private void handleHeartbeat(WebSocketSession session) {
        JSONObject response = new JSONObject();
        response.put("type", "heartbeat");
        response.put("data", System.currentTimeMillis());
        try {
            session.sendMessage(new TextMessage(response.toJSONString()));
        } catch (IOException e) {
            log.error("[WS_HEARTBEAT_ERROR] {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String uri = session.getUri().toString();
        Long roomId = extractRoomId(uri);

        CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
        sessionUserMap.remove(session.getId());

        log.info("[WS_DANMAKU_DISCONNECT] roomId={}, sessionId={}", roomId, session.getId());
    }

    public void broadcastToRoom(Long roomId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("[WS_BROADCAST_ERROR] {}", e.getMessage());
                    }
                }
            }
        }
    }

    private void sendError(WebSocketSession session, String message) {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("message", message);
        try {
            session.sendMessage(new TextMessage(error.toJSONString()));
        } catch (IOException e) {
            log.error("[WS_SEND_ERROR] {}", e.getMessage());
        }
    }

    private Long extractRoomId(String uri) {
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("ws".equals(parts[i]) && i + 2 < parts.length) {
                try {
                    return Long.parseLong(parts[i + 1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        String[] pathParts = uri.substring(uri.indexOf("/ws/danmaku/") + 13).split("[?]");
        return Long.parseLong(pathParts[0]);
    }

    private String getTokenFromSession(WebSocketSession session) {
        Map<String, Object> attrs = session.getAttributes();
        return (String) attrs.get("token");
    }

    public int getRoomViewerCount(Long roomId) {
        CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
        return sessions != null ? sessions.size() : 0;
    }
}