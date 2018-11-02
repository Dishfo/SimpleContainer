package com.sicnu.cs.servlet.basis;

import javax.servlet.http.HttpServletMapping;
import java.net.InetAddress;

public class ServletPosition {
    private InetAddress host;
    private String contextPath;
    private HttpServletMapping servletMapping;


    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public HttpServletMapping getServletMapping() {
        return servletMapping;
    }

    public void setServletMapping(HttpServletMapping servletMapping) {
        this.servletMapping = servletMapping;
    }
}
