package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.BaseContainer;
import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.RequestChannel;
import com.sicnu.cs.servlet.http.ServletRegistrationImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleServletWrapper extends
        BaseContainer implements Servlet{

    private RequestChannel requestChannel;
    private ServletConfig config;
    private boolean loadOnStarted=false;

    private String servletName;
    private Servlet real;
    private boolean isAsyncSupport;
    private Class<? extends Servlet> servletClass;
    private Set<String> urls;
    private AtomicInteger isinited;
    private HashMap<String,String> initParameters;
    private MultipartConfigElement multipartConfig;


    public SimpleServletWrapper(RequestChannel requestChannel,
                                Class<? extends Servlet> servletClass) {
        real=null;
        this.requestChannel = requestChannel;
        this.servletClass = servletClass;
    }

    public SimpleServletWrapper(RequestChannel requestChannel, Servlet real) {
        this.requestChannel = requestChannel;
        this.real = real;
        servletClass=real.getClass();
    }

    @Override
    protected void initInteral() {

        isAsyncSupport=false;
        initParameters=new HashMap<>();
        isinited=new AtomicInteger(0);
        urls=new HashSet<>();

        if (real==null){
            try {
                real=servletClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                isinited.set(2);
            }
        }

    }


    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public Servlet getReal() {
        return real;
    }

    @Override
    public synchronized void init(ServletConfig config) throws ServletException {
        if (isinited.get()==2||real==null){
            throw new ServletException("this servlet is not " +
                    "available");
        }

        if (isinited.get()==1){
            return;
        }

        this.config=config;
        real.init(config);

        isinited.set(1);
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (!(req instanceof HttpServletRequest)
                ||!(res instanceof HttpServletResponse)){
            throw new ServletException("this container support http process only");
        }
        requestChannel.through((HttpServletRequest) req
                ,(HttpServletResponse) res);
        real.service(req,res);
    }

    @Override
    public String getServletInfo() {
        if (isavailable()){
            return " ";
        }
        return config.getServletName()+" in "+
                config.getServletContext().getContextPath();
    }

    @Override
    public void addChild(Container container) {
        throw new UnsupportedOperationException(" a class " +
                "didn't support add child");
    }

    public String  setInitParameter(String name,String val){
       return initParameters.put(name,val);
    }

    public String getInitParameter(String name){
        return initParameters.get(name);
    }

    public Map<String,String> getInitParameters(){
        return Collections.unmodifiableMap(initParameters);
    }


    public void setLoadOnStart(boolean load){
        loadOnStarted=load;
    }

    public boolean isLoadOnStart(){
        return loadOnStarted;
    }

    @Override
    public void destroy() {
        real.destroy();
    }

    public ServletRegistration.Dynamic createRegistration(){
        return new ServletRegistrationImpl(servletClass,this);
    }

    public Set<String> getMappings(){
        return Collections.unmodifiableSet(urls);
    }

    public void addMapping(String url){
        if (url==null||url.trim().equals("")||
            urls.contains(url)){
            return;
        }
        urls.add(url);
    }

    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.multipartConfig=multipartConfig;
    }

    public boolean isAsyncSupport() {
        return isAsyncSupport;
    }

    public void setAsyncSupport(boolean asyncSupport) {
        isAsyncSupport = asyncSupport;
    }

    public boolean isavailable(){
        return isinited.get()==1;
    }
}
