package com.cs.sicnu.http;

import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 管理httpsession 实例
 */
class HttpSessionManager {
    private ConcurrentHashMap<String, HttpSession> sessions=new ConcurrentHashMap<>();
    private Context context;

    HttpSessionManager(Context context) {
        this.context = context;
    }

    HttpSession getSession(String id){
        return sessions.get(id);
    }

    HttpSession createNewSession(){
        HttpSession session=new HttpSessioImpl(randomId(),context);
        sessions.put(session.getId(),session);
        return session;
    }

    private String randomId(){
        return UUID.randomUUID().toString();
    }

    HttpSession remove(String id){
        return sessions.remove(id);
    }

    HttpSession add(String id,HttpSession session){
        HttpSession res= sessions.putIfAbsent(id,session);
        if (res==null){
            return session;
        }else {
            return null;
        }
    }
}
