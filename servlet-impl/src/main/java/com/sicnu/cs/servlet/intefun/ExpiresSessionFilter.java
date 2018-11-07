package com.sicnu.cs.servlet.intefun;

import com.sicnu.cs.servlet.http.InteralHttpServletRequest;
import com.sicnu.cs.servlet.http.InteralHttpServletResponse;
import com.sicnu.cs.servlet.http.SessionManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

public class ExpiresSessionFilter extends InteralFilter{

    private SessionManager manager;

    public ExpiresSessionFilter(SessionManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doFilter(InteralHttpServletRequest req, InteralHttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String expires=req.getExpiresSessionId();
        if (expires!=null){
            manager.removeSession(expires);
        }
    }
}
