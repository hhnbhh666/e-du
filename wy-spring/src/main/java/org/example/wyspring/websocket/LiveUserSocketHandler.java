package org.example.wyspring.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.wyspring.service.LiveService;
import org.example.wyspring.utils.JwtUtils;
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
 * 直播间用户WebSocket处理器（用于直播间人数统计和实时通知）
 */
@Slf4j
@Component
public class LiveUserSocketHandler extends TextWebSocketHandler {

    private final Map<Long, CopyOnWriteArraySet<WebSocketSession>> roomUserSessions = new ConcurrentHashMap<>();
    private final Map<String, JSONObject> sessionInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private LiveService liveService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long roomId = extractRoomId(session.getUri().toString());
        String token = getTokenFromSession(session);

        JSONObject userInfo = new JSONObject();
        userInfo.put("userId", jwtUtils.getUserId(token));
        userInfo.put("nickname", jwtUtils.getPhone(token));
        userInfo.put("enterTime", System.currentTimeMillis());

        roomUserSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionInfoMap.put(session.getId(), userInfo);

        broadcastUserCount(roomId);

        liveService.enterRoom(userInfo.getLong("userId"), roomId);

        log.info("[WS_USER_CONNECT] roomId={}, userId={}, sessionId={}",
                roomId, userInfo.get("userId"), session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = JSON.parseObject(message.getPayload());
        String type = json.getString("type");

        Long roomId = extractRoomId(session.getUri().toString());

        switch (type) {
            case "heartbeat":
                handleHeartbeat(session, roomId);
                break;
            case "like":
                handleLike(session, roomId);
                break;
            case "product_click":
                handleProductClick(session, json);
                break;
            default:
                break;
        }
    }

    private void handleHeartbeat(WebSocketSession session, Long roomId) {
        JSONObject response = new JSONObject();
        response.put("type", "heartbeat");
        response.put("data", System.currentTimeMillis());
        response.put("viewerCount", getRoomViewerCount(roomId));
        try {
            session.sendMessage(new TextMessage(response.toJSONString()));
        } catch (IOException e) {
            log.error("[WS_HEARTBEAT_ERROR] {}", e.getMessage());
        }
    }

    private void handleLike(WebSocketSession session, Long roomId) {
        JSONObject userInfo = sessionInfoMap.get(session.getId());
        if (userInfo != null) {
            liveService.likeRoom(userInfo.getLong("userId"), roomId);

            JSONObject broadcast = new JSONObject();
            broadcast.put("type", "like");
            broadcast.put("data", userInfo.getLong("userId"));
            broadcastToRoom(roomId, broadcast.toJSONString());
        }
    }

    private void handleProductClick(WebSocketSession session, JSONObject json) {
        JSONObject userInfo = sessionInfoMap.get(session.getId());
        if (userInfo != null) {
            Long productId = json.getLong("productId");

            JSONObject broadcast = new JSONObject();
            broadcast.put("type", "product_click");
            broadcast.put("data", JSONObject.of(
                    "userId", userInfo.getLong("userId"),
                    "nickname", userInfo.getString("nickname"),
                    "productId", productId
            ));

            Long roomId = extractRoomId(session.getUri().toString());
            broadcastToRoom(roomId, broadcast.toJSONString());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long roomId = extractRoomId(session.getUri().toString());
        JSONObject userInfo = sessionInfoMap.remove(session.getId());

        CopyOnWriteArraySet<WebSocketSession> sessions = roomUserSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomUserSessions.remove(roomId);
            }
        }

        if (userInfo != null) {
            liveService.leaveRoom(userInfo.getLong("userId"), roomId);
        }

        broadcastUserCount(roomId);

        log.info("[WS_USER_DISCONNECT] roomId={}, sessionId={}", roomId, session.getId());
    }

    public void broadcastToRoom(Long roomId, String message) {
        CopyOnWriteArraySet<WebSocketSession> sessions = roomUserSessions.get(roomId);
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

    private void broadcastUserCount(Long roomId) {
        JSONObject broadcast = new JSONObject();
        broadcast.put("type", "viewer_count");
        broadcast.put("data", getRoomViewerCount(roomId));
        broadcastToRoom(roomId, broadcast.toJSONString());
    }

    private int getRoomViewerCount(Long roomId) {
        CopyOnWriteArraySet<WebSocketSession> sessions = roomUserSessions.get(roomId);
        return sessions != null ? sessions.size() : 0;
    }

    private Long extractRoomId(String uri) {
        String[] pathParts = uri.substring(uri.indexOf("/ws/live/") + 9).split("[?]");
        return Long.parseLong(pathParts[0]);
    }

    private String getTokenFromSession(WebSocketSession session) {
        Map<String, Object> attrs = session.getAttributes();
        return (String) attrs.get("token");
    }
}