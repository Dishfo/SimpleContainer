package com.sicnu.cs.servlet.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


//todo 线程同步
public class SessionManager implements SessionAcess{

    private ConcurrentHashMap<String,HttpSession> sessions;
    private ServletContext context;

    public SessionManager(ServletContext context) {
        this.context = context;
        sessions=new ConcurrentHashMap<>();
    }

    @Override
    public synchronized HttpSession getSession(String id) {
        HttpSessionImpl session= (HttpSessionImpl) sessions.get(id);
        long tacs=System.currentTimeMillis();

        if (session==null){
            return null;
        }

        synchronized (session){
            if (tacs-session.getLastAccessedTime()>
                    session.getMaxInactiveInterval()*1000){
                session.invalidate();
                removeSession(id);
                return null;
            }else {
                session.access=tacs;
            }
        }
        return session;
    }

    @Override
    public String createSession() {
        HttpSession session=new HttpSessionImpl(context,createId());
        sessions.put(session.getId(),session);
        return session.getId();
    }

    /**
     * @param sessionId will change this session's id
     * @return string new id or null this session is inactive
     */
    @Override
    public synchronized String changeId(String sessionId) {
        HttpSessionImpl session= (HttpSessionImpl) getSession(sessionId);
        if (session == null) {
            return null;
        }else {
            synchronized (session){
                if (!sessions.contains(session)){
                    return null;
                }
                session.id=createId();
                sessions.remove(sessionId);
                sessions.put(session.id,session);
                return session.id;
            }
        }
    }

    private void removeSession(String id){
        sessions.remove(id);
    }

    private String createId(){
        return UUID.randomUUID().toString();
    }

}
