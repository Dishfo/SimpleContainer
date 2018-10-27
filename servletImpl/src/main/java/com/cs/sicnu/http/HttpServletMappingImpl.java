package com.cs.sicnu.http;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.MappingMatch;

public class HttpServletMappingImpl implements HttpServletMapping {

    private String matchValue;
    private String pattern;
    private String servlet;
    private MappingMatch mappingMatch;

    public HttpServletMappingImpl(String matchValue, String pattern, String servlet, MappingMatch mappingMatch) {
        this.matchValue = matchValue;
        this.pattern = pattern;
        this.servlet = servlet;
        this.mappingMatch = mappingMatch;
    }

    @Override
    public String getMatchValue() {
        return matchValue;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public String getServletName() {
        return servlet;
    }

    @Override
    public MappingMatch getMappingMatch() {
        return mappingMatch;
    }
}
