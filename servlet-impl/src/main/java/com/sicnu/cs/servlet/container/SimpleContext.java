package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.*;
import com.sicnu.cs.servlet.http.FilterConfigFaced;
import com.sicnu.cs.servlet.http.InteralHttpServletRequest;
import com.sicnu.cs.servlet.http.InteralHttpServletResponse;
import com.sicnu.cs.servlet.http.ServletConfigFaced;
import com.sicnu.cs.servlet.intefun.ConnectionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.PortableInterceptor.HOLDING;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleContext extends BaseContext {

    private Logger logger = LogManager.getLogger(getClass().getName());

    private String contextPath;
    private String basePath;

    private ConcurrentHashMap<String, SimpleServletWrapper> servlets;
    private ConcurrentHashMap<String, SimpleFilterWrapper> filters;
    private ConcurrentHashMap<String, Object> attributies;
    private ConcurrentHashMap<String, String> initParameters;

    private ContextClassLoader contextClassLoader;
    private List<Filter> interalFilters;

    private int sessionTime=3000;

    public SimpleContext(String base_path, String contextPath) {
        this.contextPath = contextPath;
        this.basePath = base_path;
    }

    protected void initInteral() {
        filters=new ConcurrentHashMap<>();
        servlets = new ConcurrentHashMap<>();
        attributies = new ConcurrentHashMap<>();
        initParameters = new ConcurrentHashMap<>();
        interalFilters=new ArrayList<>();
        vertifyFilePath();
        contextClassLoader=ContextClassLoader.getClassLoader(classpath);
        loadInteral();
    }

    /**
     * 加载一部分context 内部使用的servlet filter
     *
     */

    private void loadInteral(){
        interalFilters.add(new ConnectionFilter());
    }

    @Override
    protected void startInteral() {

        servlets.forEach((k,v)->{
            if (!v.isAvailable()){
                try {
                    if (v.isLoadOnStart()) {
                        v.init(generateServletConfig(k));
                    }
                } catch (ServletException ignored) {

                }
            }
        });

        filters.forEach((k,v)->{
            if (v.isAvailable()){
                try {
                    v.init(generateFilterConfig(k));
                } catch (ServletException ignored) {

                }
            }
        });
    }

    private FilterConfig generateFilterConfig(String name){
        SimpleFilterWrapper wrapper=filters.get(name);
        return new FilterConfigFaced(this,wrapper);
    }


    private ServletConfig generateServletConfig(String name){
        SimpleServletWrapper wrapper=servlets.get(name);
        return new ServletConfigFaced(this,wrapper);
    }

    private String classpath;
    private String webxml;

    private void vertifyFilePath() {
        String filepath = basePath.endsWith(File.separator) ?
                basePath : basePath + File.separator;

        classpath = filepath + WebAppConstant.CLASS_DIR;
        webxml = filepath + WebAppConstant.WEB_XML;
    }


    protected void stopInteral() {}

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public ServletContext getContext(String uripath) {
        Host host = (Host) getParent();
        return host.findContext(uripath);
    }

    @Override
    public int getMajorVersion() {
        return 4;
    }

    @Override
    public int getMinorVersion() {
        return 4;
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 4;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 4;
    }

    @Override
    public String getMimeType(String file) {
        return null;
    }

    @Override
    public Set<String> getResourcePaths(String path) {
        throw new UnsupportedOperationException("getResourcePaths()");
    }

    @Override
    public URL getResource(String path) {
        throw new UnsupportedOperationException("getResourcePaths()");
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        throw new UnsupportedOperationException("getResourceAsStream()");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    private RequestDispatcher getServletDispatcherTypeByName(String name) {
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return getServletDispatcherTypeByName(name);
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        SimpleServletWrapper wrapper = servlets.get(name);
        if (wrapper == null||!wrapper.isAvailable()) {
            throw new ServletException("can't find servlet");
        }
        return wrapper.getReal();
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return new IteratorWrapper<>(servlets.values().iterator());
    }

    @Override
    public Enumeration<String> getServletNames() {
        return servlets.keys();
    }

    @Override
    public void log(String msg) {
        throw new UnsupportedOperationException("log()");
    }

    @Override
    public void log(Exception exception, String msg) {
        throw new UnsupportedOperationException("log()");
    }

    @Override
    public void log(String message, Throwable throwable) {
        throw new UnsupportedOperationException("log()");
    }

    @Override
    public String getRealPath(String path) {
        return basePath;
    }

    @Override
    public String getServerInfo() {
        throw new UnsupportedOperationException("getServerInfo()");
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return initParameters.keys();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        String res = initParameters.put(name, value);
        return res != null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributies.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return attributies.keys();
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributies.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        attributies.remove(name);
    }

    @Override
    public String getServletContextName() {
        throw new UnsupportedOperationException("getServletContextName()");
    }

    /**
     * 等待特定的加载器实现 后再实现这些函数
     *
     * @param servletName name of servlet
     * @param className   servlet class
     * @return inerface of edit opt of servlet
     */

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        try {
            Class cls=contextClassLoader.loadClass(className);
            if (Servlet.class.isAssignableFrom(cls)){
                return addServlet(servletName,cls);
            }else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        SimpleServletWrapper wrapper = new SimpleServletWrapper(requestChannel(),
                 servlet,servletName);
        Object res = servlets.putIfAbsent(servletName, wrapper);

        if (res==null){
            addChild(wrapper);
        }

        return servlets.get(servletName);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        SimpleServletWrapper wrapper = servlets.get(servletName);
        if (!HttpServlet.class.isAssignableFrom(servletClass)) {
            return null;
        }

        if (wrapper == null) {
            try {
                Servlet servlet = createServlet(servletClass);
                return addServlet(servletName, servlet);
            } catch (ServletException e) {
                return null;
            }
        }

        return servlets.get(servletName);
    }

    @Override
    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        throw new UnsupportedOperationException("addJspFile()");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        T servlet;
        if (!HttpServlet.class.isAssignableFrom(clazz)) {
            throw new ServletException("container support http only");
        }

        try {
            servlet = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("create servlet instance failed", e);
        }

        return servlet;
    }

    private RequestChannel requestChannel() {
        return null;
    }


    private <T extends Servlet> String getServletName(Class<T> clazz) throws ServletException {
        String name;
        WebServlet webServlet = clazz.getAnnotation(WebServlet.class);
        name = webServlet.name();
        if (name.equals("")) {
            throw new ServletException();
        }
        return name;
    }



    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return servlets.get(servletName);
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        try {
            Class cls=contextClassLoader.loadClass(className);
            if (Filter.class.isAssignableFrom(cls)){
                return null;
            }
            return addFilter(filterName,cls);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        SimpleFilterWrapper wrapper=new SimpleFilterWrapper(filter,filterName,new InteralServletAcess());
        if (filters.putIfAbsent(filterName,wrapper)==null){
            return wrapper;
        }
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        try {
            return addFilter(filterName,createFilter(filterClass));
        } catch (ServletException e) {
            return null;
        }
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        T filter;
        try {
            filter=clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("filter create failed",e);
        }
        return filter;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return filters.get(filterName);
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return Collections.unmodifiableMap(filters);
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String className) {}

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return contextClassLoader;
    }

    @Override
    public void declareRoles(String... roleNames) {}

    @Override
    public String getVirtualServerName() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return sessionTime;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTime=sessionTimeout;
    }

    @Override
    public String getRequestCharacterEncoding() {
        return requestCharacterEncoding.displayName();
    }

    private Charset requestCharacterEncoding = Charset.forName("UTF-8");
    private Charset responseCharacterEncoding = Charset.forName("UTF-8");

    @Override
    public void setRequestCharacterEncoding(String encoding) {
        requestCharacterEncoding = Charset.forName(encoding);
    }

    @Override
    public String getResponseCharacterEncoding() {
        return responseCharacterEncoding.displayName();
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {
        responseCharacterEncoding = Charset.forName(encoding);
    }

    @Override
    protected ServletPosition fillPosition(ServletPosition servletPosition) {
        servletPosition.setContextPath(getContextPath());
        return servletPosition;
    }

    @Override
    protected void process(HttpPair pair, ServletPosition position) {
        HttpServletRequest servletRequest=createHttpRequset(pair,position);
        HttpServletResponse servletResponse=createHttpResponse(pair,position);
        Throwable t=null;

        InteralFilterChain interalFun=new InteralFilterChain(interalFilters);
        InteralFilterChain filterChain=new InteralFilterChain(filters.values());

        try {
            interalFun.doFilter(servletRequest,servletResponse);
        } catch (IOException | ServletException ignored) {}

        try {
            filterChain.doFilter(servletRequest,servletResponse);
        } catch (Throwable throwable){
            t=throwable;
        }

        SimpleServletWrapper wrapper=
                servlets.get(position.getServletMapping());

        if (t!=null){
            errorHandle(t,servletRequest,servletResponse);
        }

        if (wrapper==null){
            servletResponse.setStatus(404);
        }else {
            try {
                if (!wrapper.isAvailable()){
                    wrapper.init(generateServletConfig(position.getServletMapping()));
                }
                wrapper.service(servletRequest,servletResponse);
            } catch (Throwable throwable){
                t=throwable;
            }
        }

        if (t!=null){
            errorHandle(t,servletRequest,servletResponse);
        }else if (servletRequest.getDispatcherType()==DispatcherType.ASYNC){
            asyncHanlde(pair,servletRequest,servletResponse);
        }else {
            competeHandle(pair,servletRequest,servletResponse);
        }
    }

    private void asyncHanlde(HttpPair pair,HttpServletRequest request,HttpServletResponse response){
        if(response.isCommitted()){
            return;
        }
    }

    private void errorHandle(Throwable t,HttpServletRequest request,HttpServletResponse response){
        if(response.isCommitted()){
            return;
        }
        try {
            response.getWriter().write(t.toString());
            response.sendError(500);
        } catch (IOException ignored) {}
    }

    private void competeHandle(HttpPair pair,HttpServletRequest request,HttpServletResponse response){
        if(response.isCommitted()){
            return;
        }
        try {response.flushBuffer();} catch (IOException ignored) {}
        try {pair.commitResponse();} catch (IOException ignored) {}
    }

    private HttpServletRequest createHttpRequset(HttpPair pair,ServletPosition position){
        InteralHttpServletRequest request=
                new InteralHttpServletRequest(pair,this);

        request.parseParameters();
        request.parseCookie();

        request.setServletMapping(null);

        return request;
    }

    private HttpServletResponse createHttpResponse(HttpPair pair,ServletPosition position){
        return new InteralHttpServletResponse(pair,this);
    }

    private class InteralServletAcess implements ServletAccess {
        @Override
        public List<String> getUrlPattern(String name) {
            List<String> urls=new ArrayList<>();
            SimpleServletWrapper wrapper=servlets.get(name);
            if (wrapper!=null){
                urls.addAll(wrapper.getMappings());
            }
            return urls;
        }

        @Override
        public String getContexPath() {
            return contextPath;
        }
    }

    @Override
    public void setParent(Container container) {
        if (!(container instanceof Host)){
            throw new IllegalArgumentException(" context's parent must be host");
        }
        super.setParent(container);
    }

    private class InteralFilterChain implements FilterChain{
        private int cur;
        private int size;
        private Filter[] wrappers;

        InteralFilterChain(Collection<? extends Filter> filterWrappers) {
            cur=0;
            size=filterWrappers.size();
            wrappers=filterWrappers.toArray(new Filter[]{});
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (cur>=size){
                return;
            }
            wrappers[cur++].doFilter(request,response,this);
        }
    }
}
