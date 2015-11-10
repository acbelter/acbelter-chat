package com.acbelter.chat.net;

import com.acbelter.chat.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SessionManager {
    private Map<Long, Session> sessionMap;
    private Map<Long, Long> userSession;
    private AtomicLong sessionCounter = new AtomicLong(0);

    public SessionManager() {
        sessionMap = new HashMap<>();
        userSession = new HashMap<>();
    }

    public Session createSession() {
        Long id = sessionCounter.getAndIncrement();
        Session session = new Session(id);
        sessionMap.put(id, session);
        return session;
    }

    public Session getSession(Long id) {
        return sessionMap.get(id);
    }

    public void registerUser(Long userId, Long sessionId) {
        userSession.put(userId, sessionId);
    }

    public Session getSessionByUser(Long userId) {
        return sessionMap.get(userSession.get(userId));
    }
}
