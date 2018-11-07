package com.sicnu.cs.servlet.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


//todo 线程同步 及时移除过期session
public class SessionManager implements SessionAcess{

    private Logger logger= LogManager.getLogger(getClass().getName());
    private ConcurrentHashMap<String,HttpSession> sessions;
    private ServletContext context;

    private static final int DEFAULT_HOLD=50;
    private AtomicInteger createhit;
    private int threadhold;


    public SessionManager(ServletContext context,int threadhold) {
        Objects.requireNonNull(context);
        this.context = context;
        sessions=new ConcurrentHashMap<>();
        createhit=new AtomicInteger(0);
        this.threadhold=threadhold;
    }

    public SessionManager(ServletContext context) {
        this(context,DEFAULT_HOLD);
    }

    @Override
    public synchronized HttpSession getSession(String id) {
        HttpSessionImpl session= (HttpSessionImpl) sessions.get(id);
        long tacs=System.currentTimeMillis();

        if (session==null){
            return null;
        }

        synchronized (session){
            if (isExpires(session,tacs)){
                logger.debug(session.getId());
                session.invalidate();
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
        session.setMaxInactiveInterval(context.getSessionTimeout());
        clearExpires();
        return session.getId();
    }

    private void clearExpires(){
        int old=createhit.get();
        while (!createhit.compareAndSet(old,old+1)){
            old=createhit.get();
        }

        old=createhit.get();
        if (old>threadhold){
            if (createhit.compareAndSet(old,0)){
                sessions.forEach((s, httpSession) -> {
                    synchronized (httpSession){
                        if (isOldExpires(httpSession,System.currentTimeMillis())){
                            httpSession.invalidate();
                            sessions.remove(s);
                        }
                    }
                });
            }
        }
    }

    private boolean isExpires(HttpSession session,long nacs){
        return nacs-session.getLastAccessedTime()>1000*session.getMaxInactiveInterval();
    }

    private boolean isOldExpires(HttpSession session,long nacs){
        return nacs-session.getLastAccessedTime()>1500*session.getMaxInactiveInterval();
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

    public void removeSession(String id){
        sessions.remove(id);
    }

    private String createId(){
        return UUID.randomUUID().toString();
    }

}
