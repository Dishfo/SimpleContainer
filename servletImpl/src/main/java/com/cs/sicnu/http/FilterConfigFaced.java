package com.cs.sicnu.http;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.util.Enumeration;
import java.util.HashMap;

public class FilterConfigFaced implements FilterConfig {

    private Context context;
    private Class<? extends Filter> filterCls;
    private HashMap<String,String> initParameters;

    private WebFilter webFilter;

    FilterConfigFaced(Context context,
                             Class<? extends Filter> filterCls) {
        this.context = context;
        this.filterCls = filterCls;
        initParameters=new HashMap<>();
        webFilter=filterCls.getAnnotation(WebFilter.class);
        if (webFilter==null){
            throw new IllegalStateException("the filter class is" +
                    "invaild class");
        }

        WebInitParam[] params=webFilter.initParams();
        for (WebInitParam param:params){
            initParameters.put(param.name(),param.value());
        }
    }

    @Override
    public String getFilterName() {
        return webFilter.filterName();
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return
                new Context.IteratorEnumeration<>(initParameters.keySet().iterator());
    }
}



















