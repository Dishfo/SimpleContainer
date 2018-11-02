package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpFilter;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class SimpleContext extends BaseContext{

    private Logger logger = LogManager.getLogger(getClass().getName());

    private String contextPath;
    private String basePath;

    private ConcurrentHashMap<String, Servlet> servlets;
    private ConcurrentHashMap<String, Object> attributies;
    private ConcurrentHashMap<String, String> initParameters;
    private ConcurrentLinkedQueue<HttpFilter> filters;

    public SimpleContext(String base_path, String contextPath) {
        this.contextPath = contextPath;
        this.basePath = base_path;
    }

    protected void startInteral() {
        Container[] children = getChilds();
    }

    protected void initInteral() {
        vertifyFilePath();
    }


    private String classpath;
    private String webxml;

    private void vertifyFilePath(){
        String filepath= basePath.endsWith(File.separator)?
                basePath : basePath +File.separator;


    }


    protected void stopInteral() {

    }

    public String basePath() {
        return basePath;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public ServletContext getContext(String uripath) {
        throw new UnsupportedOperationException("getContext");
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
    public URL getResource(String path) throws MalformedURLException {
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

    private RequestDispatcher getServletDispatcherTypeByName(String name){
        return null;
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        return  getServletDispatcherTypeByName(name);
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        return servlets.get(name);
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return new IteratorEnumeration<>(servlets.values().iterator());
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
        if ("classpath".equals(path)) {
            return basePath;
        } else {
            throw new UnsupportedOperationException("now ,i can't parse this path");
        }
    }

    @Override
    public String getServerInfo() {
        throw new UnsupportedOperationException("getRealPath()");
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
        synchronized (initParameters) {
            String v = initParameters.get(name);
            if (v == null) {
                initParameters.put(name, value);
            } else {
                return false;
            }
        }
        return false;
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
     * @param servletName
     * @param className
     * @return
     */
    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        synchronized (servlets) {
            if (servlets.containsKey(servletName)) {
                return null;
            } else {

                servlets.put(servletName, servlet);
            }
        }
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        try {
            Servlet servlet = createServlet(servletClass);
            addServlet(servletName, servlet);
        } catch (ServletException e) {
            logger.warn("add servlet fialed with class");
        }
        return null;
    }

    /////////////////////////////////////////////
    //////////////////////////////////////////////
    //////////////////////////////////////////////
    //////////////////////////////////////////////

    @Override
    public ServletRegistration.Dynamic addJspFile(String servletName, String jspFile) {
        throw new UnsupportedOperationException("addJspFile()");
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        T servlet = null;
        try {
            servlet = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException(e);
        }

        WebServlet webServlet = clazz.getAnnotation(WebServlet.class);
        if (webServlet == null) {
            throw new ServletException("the servlet don't need load with annotatin");
        }
        String[] urlpatterns = Arrays.stream(webServlet.urlPatterns()).map(s -> {
            return contextPath + s;
        }).collect(Collectors.toList()).toArray(new String[]{});




        return servlet;
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
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    /**
     * 当前不支持这些特性
     *
     * @param filterName
     * @param className
     * @return
     */
    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        if (HttpFilter.class.isAssignableFrom(filterClass)){
            try {
                HttpFilter filter= (HttpFilter)
                        createFilter(filterClass);
                if (filter!=null){
                    filters.add(filter);
                }
            } catch (ServletException ignored) {}
        }
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {

        return null;
    }


    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

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
    public void addListener(String className) {

    }

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
        return null;
    }

    @Override
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int sessionTimeout) {

    }

    @Override
    public String getRequestCharacterEncoding() {
        return requestCharacterEncoding.displayName();
    }

    private Charset requestCharacterEncoding=Charset.forName("UTF-8");
    private Charset responseCharacterEncoding=Charset.forName("UTF-8");
    @Override
    public void setRequestCharacterEncoding(String encoding) {
        requestCharacterEncoding=Charset.forName(encoding);
    }

    @Override
    public String getResponseCharacterEncoding() {
        return responseCharacterEncoding.displayName();
    }

    @Override
    public void setResponseCharacterEncoding(String encoding) {
        responseCharacterEncoding=Charset.forName(encoding);
    }


    static class IteratorEnumeration<E> implements Enumeration<E> {

        Iterator<? extends E> iterator;

        public IteratorEnumeration(Iterator<? extends E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public E nextElement() {
            return iterator.next();
        }

    }

}
