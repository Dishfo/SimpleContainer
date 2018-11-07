package com.sicnu.cs.servlet.basis;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ServletPosition {
    private List<InetAddress> host=new ArrayList<>();
    private String contextPath;
    private String  servletName;


    public List<InetAddress> getHost() {
        return host;
    }

    public void addHost(InetAddress host) {
        this.host.add(host);
    }

    public void setHost(List<InetAddress> host) {
        this.host = host;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String  servletName) {
        this.servletName = servletName;
    }
}
