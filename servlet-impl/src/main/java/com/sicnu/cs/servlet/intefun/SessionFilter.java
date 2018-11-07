package com.sicnu.cs.servlet.intefun;

import com.sicnu.cs.servlet.container.SimpleContext;
import com.sicnu.cs.servlet.http.InteralHttpServletRequest;
import com.sicnu.cs.servlet.http.InteralHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class SessionFilter extends InteralFilter {

    private SimpleContext context;

    public SessionFilter(SimpleContext context) {
        this.context = context;
    }

    @Override
    protected void doFilter(InteralHttpServletRequest req, InteralHttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getSession()==null){
            HttpSession session=req.getSession(true);

            setCookie(session,res);
        }
        super.doFilter(req, res, chain);
    }


    private void setCookie(HttpSession session,HttpServletResponse response){
        Cookie cookie=new Cookie("sessionId",session.getId());
        cookie.setMaxAge(session.getMaxInactiveInterval());
        response.addCookie(cookie);
    }
}
