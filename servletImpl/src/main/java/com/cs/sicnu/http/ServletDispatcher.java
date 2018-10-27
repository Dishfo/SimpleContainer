package com.cs.sicnu.http;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * 对servlet 容器进行包装
 *
 */
public class ServletDispatcher extends ServletContainer implements RequestDispatcher {

    public ServletDispatcher(HttpServlet realServlet, ServletConfig config) {
        super(realServlet, config);
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (request instanceof MappedServletRequest){
            ((MappedServletRequest) request).dispatcherType=DispatcherType.FORWARD;
        }
        service(request,response);
        response.flushBuffer();
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (request instanceof MappedServletRequest){
            ((MappedServletRequest) request).dispatcherType=DispatcherType.INCLUDE;
        }
        if (request instanceof HttpServletResponse)
            service(request,new UnmodifyResponse((HttpServletResponse) response));
        else{
            throw new ServletException("only support httpServlet");
        }
    }


    private static class UnmodifyResponse implements HttpServletResponse{

        private HttpServletResponse response;

        public UnmodifyResponse(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public void addCookie(Cookie cookie) {}

        @Override
        public boolean containsHeader(String name) {
            return response.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return null;}

        @Override
        public String encodeRedirectURL(String url) {
            return null;
        }

        @Override
        public String encodeUrl(String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return null;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {}

        @Override
        public void sendError(int sc) throws IOException {}

        @Override
        public void sendRedirect(String location) throws IOException {}

        @Override
        public void setDateHeader(String name, long date) {}

        @Override
        public void addDateHeader(String name, long date) {

        }

        @Override
        public void setHeader(String name, String value) {

        }

        @Override
        public void addHeader(String name, String value) {

        }

        @Override
        public void setIntHeader(String name, int value) {

        }

        @Override
        public void addIntHeader(String name, int value) {

        }

        @Override
        public void setStatus(int sc) {

        }

        @Override
        public void setStatus(int sc, String sm) {

        }

        @Override
        public int getStatus() {
            return response.getStatus();
        }

        @Override
        public String getHeader(String name) {
            return response.getHeader(name);
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return response.getHeaders(name);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return response.getHeaderNames();
        }

        @Override
        public String getCharacterEncoding() {
            return response.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return response.getContentType();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return response.getOutputStream();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return response.getWriter();
        }

        @Override
        public void setCharacterEncoding(String charset) { }

        @Override
        public void setContentLength(int len) { }

        @Override
        public void setContentLengthLong(long len) { }

        @Override
        public void setContentType(String type) {

        }

        @Override
        public void setBufferSize(int size) {

        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() throws IOException {

        }

        @Override
        public void resetBuffer() {

        }

        @Override
        public boolean isCommitted() {
            return response.isCommitted();
        }

        @Override
        public void reset() {

        }

        @Override
        public void setLocale(Locale loc) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }
    }

}
