package com.sicnu.cs.servlet.intefun;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.sicnu.cs.servlet.http.InteralHttpServletRequest;
import com.sicnu.cs.servlet.http.InteralHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

public class ConnectionFilter extends InteralFilter {
    public static final String TAG="ConnectionContrlFilter";

    @Override
    protected void doFilter(InteralHttpServletRequest req, InteralHttpServletResponse resp,
                            FilterChain chain) throws IOException, ServletException {
        String connctl=req.getHeader(HttpHeadConstant.H_CONN);
        if (connctl==null){
            connctl="";
        }
        if (connctl.compareToIgnoreCase(HttpHeadConstant.CONN_KEEP_ALIVE)==0){
            resp.addHeader(HttpHeadConstant.H_CONN,HttpHeadConstant.CONN_KEEP_ALIVE);
        }else {
            resp.addHeader(HttpHeadConstant.H_CONN,HttpHeadConstant.CONN_CLOSE);
        }
        super.doFilter(req, resp, chain);
    }

}
