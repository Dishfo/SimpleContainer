package com.sicnu.cs.servlet.http;

import com.sicnu.cs.servlet.basis.IteratorWrapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Enumeration;

public class ServletConfigFaced implements ServletConfig {

    private ServletContext context;
    private ServletRegistration.Dynamic registration;

    public ServletConfigFaced(ServletContext context,
                              ServletRegistration.Dynamic registration) {
        this.context = context;
        this.registration = registration;
    }

    @Override
    public String getServletName() {
        return registration.getName();
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getInitParameter(String name) {
        return registration.getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new IteratorWrapper<>(registration.
                getInitParameters().keySet().iterator());
    }
}




























