package com.sicnu.cs.servlet.basis.map;

import javax.servlet.http.MappingMatch;
import java.net.InetAddress;

/**
 * 用于描述servlet的查找结果
 *
 */
public class ServletSearch {
   private boolean found;
   private InetAddress host;
   private String contextPath;
   private String servletName;
   private String matchUrl;
   private String matchValue;
   private MappingMatch mappingMatch;

    public ServletSearch(boolean found) {
        this.found = found;
    }

    public boolean isFound() {
        return found;
    }

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

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getMatchUrl() {
        return matchUrl;
    }

    public void setMatchUrl(String matchUrl) {
        this.matchUrl = matchUrl;
    }

    public String getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }

    public MappingMatch getMappingMatch() {
        return mappingMatch;
    }

    public void setMappingMatch(MappingMatch mappingMatch) {
        this.mappingMatch = mappingMatch;
    }
}
