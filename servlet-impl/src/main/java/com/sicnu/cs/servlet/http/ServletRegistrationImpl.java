package com.sicnu.cs.servlet.http;

import com.sicnu.cs.servlet.container.SimpleServletWrapper;

import javax.servlet.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServletRegistrationImpl implements ServletRegistration.Dynamic {

    private Class<? extends Servlet> servletCls;
    private SimpleServletWrapper servletWrapper;

    public ServletRegistrationImpl(Class<? extends Servlet> servletCls, SimpleServletWrapper servletWrapper) {
        this.servletCls = servletCls;
        this.servletWrapper = servletWrapper;
    }

    @Override
    public Set<String> addMapping(String... urlPatterns) {

        for (String s:urlPatterns){
            servletWrapper.addMapping(s);
        }

        return servletWrapper.getMappings();
    }

    @Override
    public Collection<String> getMappings() {
        return servletWrapper.getMappings();
    }

    @Override
    public String getRunAsRole() {
        throw new UnsupportedOperationException(" run as role");
    }

    @Override
    public String getName() {
        return servletWrapper.
                getServletName();
    }

    @Override
    public String getClassName() {
        return servletCls.getName();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return servletWrapper.setInitParameter(name,value)==null;
    }

    @Override
    public String getInitParameter(String name) {
        return servletWrapper.getInitParameter(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        final HashSet<String> confict=new HashSet<>();
        initParameters.forEach((k, v) -> {
            String res=servletWrapper.setInitParameter(k,v);
            if (res!=null){
                confict.add(k);
            }
        });

        return confict;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return servletWrapper.getInitParameters();
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        servletWrapper.setLoadOnStart(loadOnStartup>0);
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        throw new UnsupportedOperationException(" can't support function about security");
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        servletWrapper.setMultipartConfig(multipartConfig);
    }

    @Override
    public void setRunAsRole(String roleName) {
        throw new UnsupportedOperationException(" run as role");
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        servletWrapper.setAsyncSupport(isAsyncSupported);
    }
}
