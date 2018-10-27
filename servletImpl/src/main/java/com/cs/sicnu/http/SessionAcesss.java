package com.cs.sicnu.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;

public class SessionAcesss {
    private static Logger logger= LogManager.getLogger(SessionAcesss.class.getName());
    private String id;
    private HttpSessionManager manager;
    private boolean inSession=false;


    public SessionAcesss(String id, HttpSessionManager manager) {
        //..............................
    }

    public SessionAcesss(String id, HttpSessionManager manager, boolean inSession) {
        this.id = id;
        this.manager = manager;
        this.inSession = inSession;

    }

    public HttpSession get(){
        if (inSession){
            return manager.getSession(id);
        }else {
            return null;
        }
    }

    public String changeID(String newID){
        if (manager.getSession(newID)!=null){
            return null;
        }else if (inSession){
            HttpSession session=manager.remove(id);
            manager.add(newID,session);
            id=newID;
            return newID;
        }
        return null;
    }


}
