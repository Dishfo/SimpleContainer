package com.cs.sicnu.http;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class SessionIdFilter extends HttpFilter {

    private HttpSessionManager manager;

    SessionIdFilter(HttpSessionManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (!(req instanceof MappedServletRequest)){
            throw new IllegalStateException("");
        }
        Cookie[] cookies=req.getCookies();
        String sessionId=null;
        for (Cookie cookie:cookies){
            if (cookie.getName().contains("sessionId")){
                sessionId=cookie.getValue();
                break;
            }
        }

        MappedServletRequest request= (MappedServletRequest) req;
        if (sessionId==null){
            HttpSession session=manager.createNewSession();
            manager.add(session.getId(),session);
            request.acesss=new SessionAcesss(session.getId(),manager,true);
            res.addCookie(new Cookie("sessionId",session.getId()));
        }else {
            HttpSession session
                    =manager.getSession(sessionId);
            if (session!=null){
                request.acesss=new SessionAcesss(sessionId,manager,true);
            }else {
                 session=manager.createNewSession();
                manager.add(session.getId(),session);
                request.acesss=new SessionAcesss(session.getId(),manager,true);
                res.addCookie(new Cookie("sessionId",session.getId()));
            }
        }
        super.doFilter(req, res, chain);
    }
}
