package com.sicnu.cs.servlet.intefun;

import com.sicnu.cs.servlet.http.InteralHttpServletRequest;
import com.sicnu.cs.servlet.http.InteralHttpServletResponse;

import javax.servlet.*;
import java.io.IOException;

public class InteralFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof InteralHttpServletRequest&&
        response instanceof InteralHttpServletResponse){
            doFilter((InteralHttpServletRequest)request,
                    (InteralHttpServletResponse) response,chain);
        }else {
            throw new IllegalArgumentException(" this is a interal filter");
        }
    }

    protected void doFilter(InteralHttpServletRequest req,InteralHttpServletResponse resp,
                            FilterChain chain)throws IOException, ServletException {
        chain.doFilter(req,resp);
    }


}
