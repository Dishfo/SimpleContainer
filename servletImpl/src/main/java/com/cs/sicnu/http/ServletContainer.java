package com.cs.sicnu.http;

import com.cs.sicnu.contextutil.Feature;
import com.cs.sicnu.contextutil.PortContainer;
import com.cs.sicnu.core.process.Container;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ServletContainer extends PortContainer implements  Servlet{

    public static final int STATE_NONE = 0x1;
    public static final int STATE_INITED = 0x2;
    public static final int STATE_FAILED=0x3;

    private volatile HttpServlet realServlet;
    private ServletConfig config;
    private AtomicInteger state=new AtomicInteger();
    private WebServlet webServletInfo;


    public ServletContainer(HttpServlet realServlet, ServletConfig config) {
        this.realServlet = realServlet;
        this.config = config;
    }

    public ServletContainer(HttpServlet realServlet) {
        this(realServlet,null);
    }


    @Override
    protected void initInteral() {
        init();
    }

    @Override
    protected void startInteral() {
        super.startInteral();
        register(getFeature(null));
    }

    public String getServletName(){
        return config.getServletName();
    }

    @Override
    protected void stopInteral() {
        stop();
    }

    public void init() {
        if (!state.compareAndSet(STATE_NONE,STATE_INITED)){
            return;
        }
        if (config==null){
            try {
                realServlet.init();
            } catch (ServletException e) {
                while (!state.compareAndSet(STATE_INITED,STATE_FAILED));
            }
        }else {
            try {
                init(config);
            } catch (ServletException e) {
                while (!state.compareAndSet(STATE_INITED,STATE_FAILED));
            }
        }
    }


    public void stop() {
        realServlet=null;
        destroy();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        realServlet.init(config);
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;

    }

    public WebServlet getWebServletInfo() {
        return webServletInfo;
    }

    public void setWebServletInfo(WebServlet webServletInfo) {
        this.webServletInfo = webServletInfo;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        realServlet.service(req,res);
    }

    @Override
    public String getServletInfo() {
        return realServlet.getServletInfo();
    }

    @Override
    public void destroy() {
        if (!state.compareAndSet(STATE_INITED,STATE_NONE)){
            return;
        }
        realServlet.destroy();

    }

    @Override
    public void addChild(Container container) {
        throw new UnsupportedOperationException("the container doesn't support add child");
    }

    public int getState(){
        return state.intValue();
    }

    @Override
    public void register(Feature feature) {
        parent.register(feature);
    }

    @Override
    protected void registerFeature(Feature feature) { }

    @Override
    protected Feature getFeature(Feature feature) {
        return new ServletFeature(getServletName(),
                webServletInfo.urlPatterns());
    }

    @Override
    protected boolean wisHandon() {
        return true;
    }

    @Override
    public void process(Object o) {
        throw new UnsupportedOperationException("this container can't " +
                "process it");
    }
}
