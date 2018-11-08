package com.sicnu.cs.servlet.basis;

import javax.servlet.http.MappingMatch;

public class HttpServletMapping implements javax.servlet.http.HttpServletMapping {

    private String matchValue;
    private String pattern;
    private String name;
    private MappingMatch mappingMatch;

    public HttpServletMapping(String matchValue, String pattern, String name, MappingMatch mappingMatch) {
        this.matchValue = matchValue;
        this.pattern = pattern;
        this.name = name;
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
        return name;
    }

    @Override
    public MappingMatch getMappingMatch() {
        return mappingMatch;
    }
}
