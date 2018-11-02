package com.sicnu.cs.servlet.container;

import com.sicnu.cs.servlet.basis.IteratorWrapper;
import com.sicnu.cs.servlet.basis.RequestChannel;
import com.sicnu.cs.servlet.basis.WebAppConstant;
import com.sicnu.cs.servlet.http.ServletConfigFaced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleContext extends BaseContext {

    private Logger logger = LogManager.getLogger(getClass().getName());

    private String contextPath;
    private String basePath;

    private ConcurrentHashMap<String, SimpleServletWrapper> servlets;
    private ConcurrentHashMap<String, Object> attributies;
    private ConcurrentHashMap<String, String> initParameters;
    private ConcurrentHashMap<String, ServletRegistration.Dynamic> registIntf;

    private ContextClassLoader contextClassLoader;


    public SimpleContext(String base_path, String contextPath) {
        this.contextPath = contextPath;
        this.basePath = base_path;
    }

    protected void initInteral() {
        servlets = new ConcurrentHashMap<>();
        attributies = new ConcurrentHashMap<>();
        initParameters = new ConcurrentHashMap<>();
        registIntf = new ConcurrentHashMap<>();
        vertifyFilePath();
        contextClassLoader=new ContextClassLoader(ClassLoader.getSystemClassLoader(),classpath);
    }


    private String classpath;
    private String webxml;

    private void vertifyFilePath() {
        String filepath = basePath.endsWith(File.separator) ?
                basePath : basePath + File.separator;

        classpath = filepath + WebAppConstant.CLASS_DIR;
        webxml = filepath + WebAppConstant.WEB_XML;
    }


    protected void stopInteral() {

    }

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
        if (wrapper == null||!wrapper.isavailable()) {
            throw new ServletException("can't find servlet");
        }
        return wrapper.getReal();
    }

    @Override
    public Enumeration<Servlet> getServlets() {
        return new IteratorWrapper<Servlet>(servlets.values().iterator());
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
                 servlet);
        Object res = servlets.putIfAbsent(servletName, wrapper);
        if (res == null) {
            registIntf.putIfAbsent(servletName,wrapper.createRegistration());
        }

        return registIntf.get(servletName);
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

        return registIntf.get(servletName);
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
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

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
        if (HttpFilter.class.isAssignableFrom(filterClass)) {
            try {
                HttpFilter filter = (HttpFilter)
                        createFilter(filterClass);
                if (filter != null) {

                }
            } catch (ServletException ignored) {
            }
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
    public void declareRoles(String... roleNames) {}

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

    private ServletConfig generateServletConfig(String servletName) {
        ServletRegistration.Dynamic dynamic = registIntf.get(servletName);
        if (dynamic != null) {
            return new ServletConfigFaced(this, dynamic);
        } else {
            return null;
        }
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


}