package com.sicnu.cs.servlet.intefun;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.sicnu.cs.servlet.container.SimpleContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ConnectionFilter extends HttpFilter {
    public static final String TAG="ConnectionContrlFilter";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String connctl=req.getHeader(HttpHeadConstant.H_CONN);
        if (connctl.compareToIgnoreCase(HttpHeadConstant.CONN_KEEP_ALIVE)==0){
            res.addHeader(HttpHeadConstant.H_CONN,HttpHeadConstant.CONN_KEEP_ALIVE);
        }else {
            res.addHeader(HttpHeadConstant.H_CONN,HttpHeadConstant.CONN_CLOSE);
        }
        super.doFilter(req, res, chain);
    }

}
