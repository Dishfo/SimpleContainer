package com.sicnu.cs.servlet.http;

import com.sicnu.cs.servlet.basis.IteratorWrapper;

import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.Enumeration;

public class FilterConfigFaced implements FilterConfig {

    private ServletContext context;
    private FilterRegistration.Dynamic dynamic;

    public FilterConfigFaced(ServletContext context, FilterRegistration.Dynamic dynamic) {
        this.context = context;
        this.dynamic = dynamic;
    }

    @Override
    public String getFilterName() {
        return dynamic.getName();
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getInitParameter(String name) {
        return dynamic.getInitParameter(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return new IteratorWrapper<>(dynamic.
                getInitParameters().keySet().iterator());
    }
}
