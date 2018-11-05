package com.sicnu.cs.servlet.http;

import javax.servlet.http.HttpSession;

public interface SessionAcess {
    HttpSession getSession(String id);
    String createSession();
    String changeId(String sessionId);
}
