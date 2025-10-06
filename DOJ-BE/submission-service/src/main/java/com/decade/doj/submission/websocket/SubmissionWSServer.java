package com.decade.doj.submission.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/submission")
public class SubmissionWSServer {

    private static final Map<Long, Session> ONLINE_SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        log.info("WebSocket 连接已建立: {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            Map<String, Object> data = JSON.parseObject(message);
            Long submissionId = Long.valueOf(data.get("submissionId").toString());
            ONLINE_SESSIONS.put(submissionId, session);
            log.info("submissionId: {} 已订阅 WebSocket 通知", submissionId);
        } catch (Exception e) {
            log.error("处理 WebSocket 消息时出错: {}", message, e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        // 清理无效的 session
        ONLINE_SESSIONS.values().removeIf(s -> !s.isOpen());
        log.info("WebSocket 连接已关闭: {}", session.getId());
    }

    public static void sendMessage(Long submissionId, String message) {
        Session session = ONLINE_SESSIONS.get(submissionId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("成功向 submissionId: {} 推送消息", submissionId);
                // 推送成功后可以移除，避免重复推送
                ONLINE_SESSIONS.remove(submissionId);
            } catch (IOException e) {
                log.error("向 submissionId: {} 推送消息失败", submissionId, e);
            }
        }
    }
}
