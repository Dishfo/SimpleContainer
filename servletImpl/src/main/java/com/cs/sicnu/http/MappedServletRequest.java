package com.cs.sicnu.http;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.protocol.ParseException;
import com.cs.sicnu.http.HttpMessageMap.RequestInfoAccess;
import com.cs.sicnu.http.contentparse.MutilPartParser;
import com.cs.sicnu.http.contentparse.NormalPart;
import com.cs.sicnu.http.contentparse.XXX_formParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

public class MappedServletRequest implements HttpServletRequest {

    private Logger logger= LogManager.getLogger(getClass().getName());
    SessionAcesss acesss;
    private RequestInfoAccess requestInfo;
    private String authType=BASIC_AUTH;
    private URI uri;

    private HashMap<String,Object> attributies;
    private HashMap<String,String[]> parameters;

    private List<Part> parts;

    private Charset characterEncoding;
    private List<Cookie> cookies;

    MappedServletRequest(RequestInfoAccess requestInfo) {
        this.requestInfo = requestInfo;
        attributies=new HashMap<>();
        parameters=new HashMap<>();
        uri=requestInfo.getUri();
    }

    @Override
    public HttpServletMapping getHttpServletMapping() {
        return requestInfo.getHttpServletMapping();
    }

    void compete() throws IOException ,ParseException{
        characterEncoding=
                Charset.forName(requestInfo.getServletContext().getRequestCharacterEncoding());
        cookies=new ArrayList<>();
        //解析query content
        parseCookie();
        parseContent();
        parseParameters();

    }

    private void parseCookie() throws ParseException{
        String cookie=getHeader(HttpHeadConstant.H_COOKIE);
        if (cookie!=null&&!cookie.equals("")){
            String[] cookies=cookie.split(";");
            for (String c:cookies){
                c=c.split(";")[0].trim();
                if (!c.equals("")){
                    String[] maps=c.split("=");
                    if (maps.length!=2){
                        throw new ParseException("cookie format error");
                    }
                    Cookie ncookie=new Cookie(maps[0].trim(),maps[1].trim());
                    this.cookies.add(ncookie);
                }
            }
        }
    }

    private void parseParameters() throws IOException{

        parseQuery();
        if (parts==null){return;}
        for (Part p:parts){
            if (p instanceof NormalPart){
                Reader reader=new InputStreamReader(p.getInputStream());
                BufferedReader bufferedReader=new BufferedReader(reader);
                String line=bufferedReader.readLine();
                if (line!=null){
                    parameters.put(p.getName(),new String[]{line});
                }
            }
        }
    }

    private void parseContent() throws IOException {
        String contenttype=getContentType();
        if (contenttype==null){
            return;
        }
        if (contenttype.startsWith(HttpHeadConstant.CT_TYPE_form)){
            String boundarys=contenttype.split(";")[1].trim();
            String boundary=boundarys.split("=")[1];
            try {
                parts=
                        new MutilPartParser().parse(getInputStream(),getContentLength(),boundary);
                logger.debug("parse all part compete");

            } catch (ParseException e) {
                throw new IOException(e);
            }
        }else if (contenttype.startsWith(HttpHeadConstant.CT_TYPE_XXX)){
            try {
                Map<String,String[]> parameters=new XXX_formParser().parse(getInputStream());
                this.parameters.putAll(parameters);
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
    }

    private static final String query_split="&";
    private static final String query_eq="=";
    private static final String para_split=",";

    private void parseQuery() {
        String query=uri.getQuery();
        if (query!=null&&query.length()>0){
            String items[]=query.split(query_split);
            for (String item:items){
                String[] maps=item.split(query_eq);
                if (maps.length==2){
                    String name=maps[0];
                    String val=maps[1];
                    String[] avals=val.split(para_split);
                    parameters.put(name,avals);
                }
            }
        }
    }

    @Override
    public String getAuthType() {
        return authType;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies.toArray(new Cookie[]{});
    }

    @Override
    public long getDateHeader(String name) {
        String headvalue=requestInfo.getHeader(name);
        return Long.parseLong(headvalue);
    }

    @Override
    public String getHeader(String name) {
        return requestInfo.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value=requestInfo.getHeader(name);
        if (value==null){
            return null;
        }else {
            return new HttpRequestWrapper.ArrayEnumeration<>(value.split(","));
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return requestInfo.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {
        String headvalue=requestInfo.getHeader(name);
        if (headvalue==null){
            return 0;
        }
        return (int) Long.parseLong(headvalue);
    }

    @Override
    public String getMethod() {
        return requestInfo.getMethod();
    }

    @Override
    public String getPathInfo() {
        return requestInfo.getPath();
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("getPathTranslated()");
    }

    @Override
    public String getContextPath() {
        return requestInfo.getContextPath();
    }

    @Override
    public String getQueryString() {
        return uri.getQuery();
    }

    @Override
    public String getRemoteUser() {
        return requestInfo.getRemoteAddress().toString();
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("getUserPrincipal()");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("getRequestedSessionId()");
    }

    @Override
    public String getRequestURI() {
        return uri.toString();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer buffer=new StringBuffer();
        try {
            buffer.append(uri.toURL().toString());
        } catch (MalformedURLException ignored) {}
        return buffer;
    }

    @Override
    public String getServletPath() {
        return uri.getPath();
    }

    //todo 一下未实现
    /**
     * 未实现
     *
     * @param create .
     * @return .
     */
    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return acesss.get();
    }

    @Override
    public String changeSessionId() {
        while (true){
            String id=UUID.randomUUID().toString();
            if (acesss.changeID(id)!=null){
                return id;
            }
        }

    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }



    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException { }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    //todo 以上未实现

    @Override
    public Object getAttribute(String name) {
        return attributies.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Context.IteratorEnumeration<>(attributies.keySet().iterator());
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding.displayName();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        Charset newset=Charset.forName(env);
    }


    @Override
    public int getContentLength() {
        int res1=getIntHeader(HttpHeadConstant.H_CONT_LEN);
        int res2=getIntHeader("Content-length");
        int res3=getIntHeader("content-length");
        return (res1>=res2?(res1>res3?res1:res3):res2);
    }

    @Override
    public long getContentLengthLong() {
        String len=getHeader(HttpHeadConstant.H_CONT_LEN);
        if (len==null){
            return 0;
        }else {
            return Long.parseLong(len);
        }
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeadConstant.H_CONT_TYPE);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return requestInfo.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        String[] paramvalue=parameters.get(name);
        if (paramvalue==null){
            return null;
        }else {
            StringBuilder builder=new StringBuilder();
            for (String s:paramvalue){
                builder.append(s)
                        .append(",");
            }
            return builder.substring(0,
                    Math.max(0,builder.length()-1));
        }

    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Context.IteratorEnumeration<>(parameters.keySet().iterator());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() {
        return requestInfo.getProtocol();
    }

    @Override
    public String getScheme() {
        return requestInfo.getSchema();
    }

    @Override
    public String getServerName() {
        return requestInfo.getHostName();
    }

    @Override
    public int getServerPort() {
        return requestInfo.getLocalPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(
                new InputStreamReader(getInputStream(),characterEncoding));
    }

    @Override
    public String getRemoteAddr() {
        return requestInfo.getRemoteAddress().toString();
    }

    @Override
    public String getRemoteHost() {
        return requestInfo.getRemoteAddress().getHostName();
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributies.put(name,o);
    }

    @Override
    public void removeAttribute(String name) {
        attributies.remove(name);
    }

    //todo 以下未实现
    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return getServletContext().getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    //todo 以上未实现

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    @Override
    public int getLocalPort() {
        return requestInfo.getLocalPort();
    }

    //todo 一下未实现

    @Override
    public ServletContext getServletContext() {
        return requestInfo.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        if (isAsyncStarted()){
            throw new IllegalStateException("async has been started ");
        }
        asyncContext=new ServletAsyncContext(servletRequest,servletResponse);
        dispatcherType=DispatcherType.ASYNC;
        return asyncContext;
    }

    private AsyncContext asyncContext=null;

    @Override
    public boolean isAsyncStarted() {
        return asyncContext!=null;
    }

    @Override
    public boolean isAsyncSupported() {
        return true;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    DispatcherType dispatcherType=DispatcherType.REQUEST;

    @Override
    public DispatcherType getDispatcherType() {
        return dispatcherType;
    }

    /**
     *
     * 用于对content 进行解析
     *
     */
    private class ContentParser{

    }



}
