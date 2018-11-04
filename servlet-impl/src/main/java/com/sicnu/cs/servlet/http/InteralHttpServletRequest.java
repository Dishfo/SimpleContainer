package com.sicnu.cs.servlet.http;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.sicnu.cs.servlet.basis.ArrayEnumeration;
import com.sicnu.cs.servlet.basis.ByteServletInputStream;
import com.sicnu.cs.servlet.basis.HttpPair;
import com.sicnu.cs.servlet.basis.ListEnumeration;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

public class InteralHttpServletRequest implements HttpServletRequest {

    private static final String[] emptystrarray=new String[]{};
    private HttpPair pair;
    private HttpServletMapping servletMapping;
    private ServletContext context;
    private HashMap<String,Object> attributes;
    private String serveltPath;
    private String characterEncoding;
    ByteServletInputStream inputStream;
    public InteralHttpServletRequest(HttpPair pair,
                                     ServletContext context) {
        this.pair = pair;
        this.context = context;
        //serveltPath=context.getContextPath()+"/"+servletMapping.getServletName();
        attributes=new HashMap<>();
        inputStream=new ByteServletInputStream(pair.getInputData());
    }

    public void setServletMapping(HttpServletMapping servletMapping) {
        this.servletMapping = servletMapping;
    }

    @Override
    public HttpServletMapping getHttpServletMapping() {
        return servletMapping;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        String val=pair.getRequsetHead(name.toLowerCase());
        if (val==null){
            return -1;
        }else {
            DateFormat dateFormat=DateFormat.getDateInstance();
            try {
               return dateFormat.parse(val).getTime();
            } catch (ParseException e) {
                throw new IllegalArgumentException("error data format");
            }
        }
    }

    @Override
    public String getHeader(String name) {
        return pair.getRequsetHead(name.toLowerCase());
    }



    @Override
    public Enumeration<String> getHeaders(String name) {
        String val=getHeader(name);
        if (val==null){
            return new ArrayEnumeration<>(emptystrarray);
        }else {
            return new ArrayEnumeration<>(val.split(HttpHeadConstant.head_value_split));
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Map<String,String> headers=pair.getRequsetHeaders();
        List<String> list = new ArrayList<>(headers.keySet());
        return new ListEnumeration<>(list);
    }

    @Override
    public int getIntHeader(String name) {
        String val=getHeader(name);
        if (val==null){
            return -1;
        }else {
            return Integer.parseInt(val);
        }
    }

    @Override
    public String getMethod() {
        return pair.getMethod();
    }

    @Override
    public String getPathInfo() {
        URI uri=pair.getRequsetUrl();
        return uri.getPath();
    }


    @Override
    public String getPathTranslated() {
        return serveltPath;
    }

    @Override
    public String getContextPath() {
        return context.getContextPath();
    }

    @Override
    public String getQueryString() {
        return pair.getRequsetUrl().getQuery();
    }

    @Override
    public String getRemoteUser() {
        return pair.getRemote().toString();
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return pair.getRequsetUrl().toString();
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer buffer=new StringBuffer();
        try {
            buffer.append(pair.getRequsetUrl().toURL().toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(" this url is illegal");
        }
        return buffer;
    }

    @Override
    public String getServletPath() {
        String url=getRequestURI();
        return url.substring(getContextPath().length());
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
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
    public void login(String username, String password) throws ServletException {}

    @Override
    public void logout() throws ServletException {}

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

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        List<String> list=new ArrayList<>(attributes.keySet());
        return new ListEnumeration<>(list);
    }

    @Override
    public String getCharacterEncoding() {
        if (characterEncoding==null){
            characterEncoding=context.getRequestCharacterEncoding();
        }
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        try {
            Charset.forName(env);
        }catch (Throwable throwable){
            throw new UnsupportedEncodingException(" error charset ");
        }
        characterEncoding=env;
    }

    @Override
    public int getContentLength() {
        return getIntHeader(HttpHeadConstant.H_CONT_LEN);
    }

    @Override
    public long getContentLengthLong() {
        String val=getHeader(HttpHeadConstant.H_CONT_LEN);
        if (val==null){
            return -1;
        }else {
            return Long.parseLong(val);
        }
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeadConstant.H_CONT_TYPE);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!inputStream.isReady()){
            throw new IOException("this stream is not available");
        }
        return inputStream;
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return pair.getRequsetVersion();
    }

    @Override
    public String getScheme() {
        return pair.getSchema();
    }

    @Override
    public String getServerName() {
        String host=getHeader(HttpHeadConstant.H_HOST);
        if (host!=null){
            return host.split(":")[0];
        }else {
            return getLocalName();
        }
    }

    @Override
    public int getServerPort() {
        String host=getHeader(HttpHeadConstant.H_HOST);
        if (host!=null){
            String[] items= host.split(":");
            if (items.length==2){
                return Integer.parseInt(items[1]);
            }else {
                return getLocalPort();
            }
        }else {
            return getLocalPort();
        }

    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getRemoteAddr() {
        return pair.getRemote().getAddress().toString();
    }

    @Override
    public String getRemoteHost() {
        return pair.getRemote().getHostName();
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.putIfAbsent(name,o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Locale getLocale() {
        return Locale.CHINA;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return new ArrayEnumeration<>(new Locale[]{Locale.CHINA});
    }

    @Override
    public boolean isSecure() {
        return getScheme().equals("https");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return pair.getRemote().getPort();
    }

    @Override
    public String getLocalName() {
        return pair.getLocalHost().getHostName();
    }

    @Override
    public String getLocalAddr() {
        return pair.getLocalHost().getAddress().toString();
    }

    @Override
    public int getLocalPort() {
        return pair.getLocalHost().getPort();
    }

    @Override
    public ServletContext getServletContext() {
        return context;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    protected DispatcherType type=DispatcherType.REQUEST;

    @Override
    public DispatcherType getDispatcherType() {
        return type;
    }
}
