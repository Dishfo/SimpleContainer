package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.RequestChannel;
import com.sicnu.cs.servlet.basis.ServletPosition;
import com.sicnu.cs.servlet.http.ServletConfigFaced;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleServletWrapper extends
        RegistraContainer implements Servlet,ServletRegistration.Dynamic{

    private RequestChannel requestChannel;
    private ServletConfig config;
    private boolean loadOnStarted=false;

    private Servlet real;

    private Set<String> urls;
    private AtomicInteger isinited;
    private HashMap<String,String> initParameters;
    private MultipartConfigElement multipartConfig;


    SimpleServletWrapper(RequestChannel requestChannel, Servlet real,String name) {
        super(name);
        this.requestChannel = requestChannel;
        this.real = real;

        initParameters=new HashMap<>();
        isinited=new AtomicInteger(0);
        urls=new HashSet<>();
        cls=real.getClass();
    }

    Servlet getReal() {
        return real;
    }

    @Override
    protected void startInteral() {
        super.startInteral();
        registerUrl(urls.toArray(new String[]{}));
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

    void initServlet()throws ServletException{
        if (isinited.compareAndSet(0,1)){
            Container context=  getParent();

            if (!(context instanceof ServletContext)){
                throw new ServletException("this servlet is not in web container ");
            }

            config=new ServletConfigFaced((ServletContext) context,
                    this);
            init(config);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (!isAvailable()){
            throw new ServletException("this servlet is not availaable");
        }

        if (!(req instanceof HttpServletRequest)
                ||!(res instanceof HttpServletResponse)){
            throw new ServletException("this container support http process only");
        }

        if (requestChannel!=null){
            requestChannel.through((HttpServletRequest) req
                    ,(HttpServletResponse) res);
        }

        real.service(req,res);
    }

    @Override
    public String getServletInfo() {
        if (!isAvailable()){
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

    @Override
    public boolean setInitParameter(String name, String value) {
        return initParameters.putIfAbsent(name,value)==null;
    }

    @Override
    public String getInitParameter(String name) {
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

    @Override
    public Set<String> addMapping(String... urlPatterns) {
        Set<String> confilct=new HashSet<>();
        List<String> tmp=new ArrayList<>();
        for (String s:urlPatterns){
            if (urls.contains(s)){
                confilct.add(s);
            }else {
                tmp.add(s);
                urls.add(s);
            }
        }
        return confilct;
    }

    public Set<String> getMappings(){
        return Collections.unmodifiableSet(urls);
    }

    @Override
    public String getRunAsRole() {
        return null;
    }

    public void addMapping(String url){
        if (url==null||url.trim().equals("")||
            urls.contains(url)){
            return;
        }

        urls.add(url);
    }

    private void registerUrl(String[] urls){
        ServletPosition position=new ServletPosition();
        position.setServletName(getName());
        onRegister(urls,position);
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStarted=(loadOnStartup>0);
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        throw new UnsupportedOperationException("can't support security");
    }

    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.multipartConfig=multipartConfig;
    }

    @Override
    public void setRunAsRole(String roleName) {
        throw new UnsupportedOperationException("can't run as role");
    }

    public boolean isAsyncSupport() {
        return isAsync;
    }

    boolean isAvailable(){
        return isinited.get()==1;
    }

    @Override
    protected boolean needAllCompete() {
        return false;
    }
}
