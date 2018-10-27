package com.cs.sicnu.http;

import com.cs.sicnu.core.protocol.Http11Constant;
import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.protocol.HttpRequest;
import com.cs.sicnu.core.utils.BytesServletInputStream;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.Principal;
import java.util.*;

/**
 * 名字虽然是wrapper
 * 但我还没想好是什么形式
 *
 */
public class HttpRequestWrapper implements HttpServletRequest {

    private HttpRequest request;
    private ServletContext context;
    private URI requestUri;

    private HashMap<String,String> headers;
    private HashMap<String,Object> attributes;
    private HashMap<String,String[]> parameters;
    private String frame;
    private ServletInputStream inputStream;
    private HttpServletMapping mapping;
    private ContentParser contentParser;

    private HttpResponseWrapper responseWrapper;

    protected void setMapping(HttpServletMapping mapping){
        this.mapping=mapping;
    }

    public HttpRequestWrapper() {

        attributes=new HashMap<>();
        parameters=new HashMap<>();
    }

    public void init(HttpRequest request){
        this.request=request;
        StringBuilder uri=new StringBuilder();
        uri.append(getScheme())
                .append("://")
                .append(request.getHost())
                .append(request.getResUrl());

        requestUri=vertifyUri(uri.toString());

        headers=new HashMap<>(request.getMap());
        String query=requestUri.getQuery();
        vertifyParameters(query);
        frame=requestUri.getFragment();



        contentParser=new ContentParser();
        //主要是post 下form-data x-www-form-urlencoded
        if (getMethod().compareToIgnoreCase(Http11Constant.post_m)==0){
            HashMap<String,String> map=contentParser.parserPostParmeters();
            for (Map.Entry<String,String> e:map.entrySet()){
                parameters.put(e.getKey(),new String[]{e.getValue()});
            }
        }
    }


    /**
     *
     * vertify uri is vaild
     *
     * @param uri the reqeuset uri
     * @throw IllegalArgumentException uri is invaild
     */

    private URI vertifyUri(String uri){
        return URI.create(uri);
    }


    /**
     * resolve query paraeters
     *
     * @param query
     *
     * @throw IllegalArgumentException the query is invaild
     */
    private void vertifyParameters(String query){
        if (query==null){
            return;
        }
        String items[]=query.split("&");
        for (String item:items){
            if (item.equals("")){
                continue;
            }
            String maps[]=item.split("=");

            if (maps.length!=2){
                throw new IllegalArgumentException("the query map is invaild");
            }
            parameters.put(maps[0],new String[]{maps[1]});
        }
    }

    public HttpResponseWrapper getResponseWrapper() {
        return responseWrapper;
    }

    public void setResponseWrapper(HttpResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }

    public HttpServletMapping getMapping() {
        return mapping;
    }

    public HttpServletMapping getHttpServletMapping() {
        return mapping;
    }

    public void setContext(Context context){
        this.context=context;
    }

    @Override
    public String getAuthType() {
        return BASIC_AUTH;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        String  value=headers.get(name);
        long res=0;
        if (value==null){
            return -1;
        }else {
            try {
                res=Long.valueOf(value);
            }catch (Throwable throwable){
                throw new IllegalArgumentException("the header can't convert to long "+name);
            }
        }
        return res;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value=headers.get(name);
        if (value==null){
            return null;
        }else {
            return new ArrayEnumeration<>(value.split(","));
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return new Context.IteratorEnumeration<>(headers.keySet().iterator());
    }

    @Override
    public int getIntHeader(String name) {
        String  value=headers.get(name);
        int res=0;
        if (value==null){
            return -1;
        }else {
            try {
                res=Integer.valueOf(value);
            }catch (Throwable throwable){
                throw new IllegalArgumentException("the header can't convert to long "+name);
            }
        }
        return res;
    }

    @Override
    public String getMethod() {

        return request.getMethod();
    }

    @Override
    public String getPathInfo() {
        return requestUri.getPath();
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return context.getContextPath();
    }

    @Override
    public String getQueryString() {
        return requestUri.getQuery();
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("getRemoteUser");
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException("isUserInRole");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("getUserPrincipal");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("getRequestedSessionId");
    }

    @Override
    public String getRequestURI() {
        return requestUri.toString();
    }

    @Override
    public StringBuffer getRequestURL() {
        try {
            return new StringBuffer(requestUri.toURL().toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("getsession");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("changeSessionId");
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
        throw new UnsupportedOperationException("authenticate");
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException("login");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("logout");
    }

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
        throw new UnsupportedEncodingException("can't support upgrade now");
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return new Context.IteratorEnumeration<>(attributes.keySet().iterator());
    }


    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return (int) request.getContent_length();
    }

    @Override
    public long getContentLengthLong() {
        return request.getContent_length();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        byte[] data=request.getData();
        return new BytesServletInputStream(data==null?new byte[]{}:data);
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name).toString();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Context.IteratorEnumeration<>(parameters.keySet().iterator());
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;

    }

    public URI getRequestUri(){
        return requestUri;
    }

    @Override
    public String getProtocol() {
        return request.getVersion();
    }

    @Override
    public String getScheme() {
        return request.getSchema();
    }

    @Override
    public String getServerName() {
        return request.getHost();
    }

    @Override
    public int getServerPort() {
        return request.getLocalPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddress().getAddress().toString();
    }

    @Override
    public String getRemoteHost() {
        return requestUri.getHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name,o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

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
        return request.getSchema().equals("https");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("getRequestDispatcher");
    }

    @Override
    public String getRealPath(String path) {
        return context.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return request.getRemoteAddress().getPort();
    }

    @Override
    public String getLocalName() {
        return request.getHost();
    }

    @Override
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("startAsync");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("startAsync");
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
        throw new UnsupportedOperationException("getAsyncContext");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("getDispatcherType");
    }


    public static class ArrayEnumeration<T> implements Enumeration<T>{

        private T[] values;
        private int pos;

        public ArrayEnumeration(T[] values) {
            Objects.requireNonNull(values);
            this.values = values;
            pos=0;
        }

        @Override
        public boolean hasMoreElements() {
            return pos==values.length;
        }

        @Override
        public T nextElement() {
            return values[pos++];
        }
    }

    private class ContentParser{

        String content_type=getContentType().trim();

        HashMap<String,String> parserPostParmeters(){
            HashMap<String,String> res=new HashMap<>();
            if (content_type.equals(HttpHeadConstant.CT_TYPE_XXX)){
                try {
                    StringBuilder builder=new StringBuilder();
                    Reader reader=getReader();
                    char charaters[]=new char[1024];
                    int len=0;
                    while ((len=reader.read(charaters))>0){
                        builder.append(charaters,0,len);
                    }
                    String params=builder.toString();
                    String[] items=params.split("&");
                    if (items.length>0){
                        for (String item:items){
                            int index=item.indexOf('=');
                            if (index<0){
                                continue;
                            }
                            String name=item.substring(0,index);
                            String value=item.substring(index+1);
                            res.put(name,value);
                        }
                    }
                } catch (IOException e) {

                }
            }
            return res;
        }


    }

}
