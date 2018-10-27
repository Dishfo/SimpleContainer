package com.cs.sicnu.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Objects;

public class ServletConfigFaced implements ServletConfig {
    private Context context;
    private String servletName;


    public ServletConfigFaced(Context context,String serletName) {
        Objects.requireNonNull(context);
        this.context = context;
        this.servletName =serletName;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getInitParameter(String name) {
        return context.getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return context.getInitParameterNames();
    }
}
