package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.ServletAccess;
import com.sicnu.cs.servlet.http.FilterConfigFaced;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleFilterWrapper extends RegistraContainer implements Filter,FilterRegistration.Dynamic{

    private Filter real;
    private FilterConfig config;
    private List<String> urls;
    private Boolean isMatchAfter;

    private ServletAccess access;
    private Set<DispatcherType> types;
    private AtomicInteger isinited;

    SimpleFilterWrapper(Filter real, String filterName,
                        ServletAccess access) {

        super(filterName);
        this.real = real;
        types=new HashSet<>();
        isinited=new AtomicInteger(0);
        urls=new ArrayList<>();
        isMatchAfter=null;
        this.access=access;
        cls=real.getClass();
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
        if (isAvailable()){
            throw new ServletException("this filter is not availaable");
        }
        if (types.contains(request.getDispatcherType())){
            real.doFilter(request,response,chain);
        }
    }



    @Override
    public void destroy() {
        real.destroy();
    }

    boolean isAvailable(){
        return isinited.get() != 1;
    }

    public boolean isAsyncSupport() {
        return isAsync;
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        fillTypes(dispatcherTypes, isMatchAfter);
        for (String sname:servletNames){
            List<String> patterns=access.getUrlPattern(sname);
            String contextPath=access.getContexPath();
            for (String s:patterns){
                addMappingForUrlPatterns(null,
                        false,contextPath+s);
            }
        }
    }

    void initFilter()throws ServletException{
        if (isinited.compareAndSet(0,1)){
            Container context=  getParent();

            if (!(context instanceof ServletContext)){
                throw new ServletException("this servlet is not in web container ");
            }

            config=new FilterConfigFaced((ServletContext) context,
                    this);
            init(config);
        }
    }

    @Override
    public Collection<String> getServletNameMappings() {
        return null;
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        fillTypes(dispatcherTypes, isMatchAfter);
        urls.addAll(Arrays.asList(urlPatterns));
    }

    private void fillTypes(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter) {
        if (this.types.size()==0){
            if (dispatcherTypes==null){
                types.add(DispatcherType.REQUEST);
            }else {
                for (DispatcherType type:dispatcherTypes){
                    types.add(type==null?DispatcherType.REQUEST:type);
                }
            }
            this.isMatchAfter=isMatchAfter;
        }
    }

    public boolean getMatchAfter() {
        return isMatchAfter;
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return null;
    }

    @Override
    protected boolean needAllCompete() {
        return false;
    }
}
