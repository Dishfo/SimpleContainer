package com.sicnu.cs.servlet.container;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleFilterWrapper extends RegistraContainer implements Filter,FilterRegistration.Dynamic{

    private Filter real;
    private FilterConfig config;
    private Class filterCls;
    private List<String> urls;

    private List<String> supportsServlets;

    private Set<DispatcherType> types;
    private AtomicInteger isinited;

    public SimpleFilterWrapper(Filter real,String filterName) {
        super(filterName);
        this.real = real;
        types=new HashSet<>();
        isinited=new AtomicInteger(0);
        urls=new ArrayList<>();
        supportsServlets=new ArrayList<>();
        filterCls=real.getClass();
    }

    @Override
    public synchronized void init(FilterConfig filterConfig) throws ServletException {
        if (isinited.get()==2||real==null){
            throw new ServletException("this filter is not " +
                    "available");
        }

        if (isinited.get()==1){
            return;
        }

        this.config=filterConfig;
        real.init(filterConfig);
        isinited.set(1);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        real.doFilter(request,response,chain);
    }

    @Override
    public void destroy() {
        real.destroy();
    }

    public boolean isAvailable(){
        return isinited.get()==1;
    }

    public boolean isAsyncSupport() {
        return isAsync;
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        if (this.types.size()==0){
            for (DispatcherType type:dispatcherTypes){
                types.add(type==null?DispatcherType.REQUEST:type);
            }
        }
    }

    @Override
    public Collection<String> getServletNameMappings() {
        return Collections.unmodifiableCollection(supportsServlets);
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        if (this.types.size()==0){
            for (DispatcherType type:dispatcherTypes){
                types.add(type==null?DispatcherType.REQUEST:type);
            }
        }
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return null;
    }

}
