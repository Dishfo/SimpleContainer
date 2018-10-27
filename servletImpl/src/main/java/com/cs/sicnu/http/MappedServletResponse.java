package com.cs.sicnu.http;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.http.HttpMessageMap.ResponseInfoAcess;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MappedServletResponse implements HttpServletResponse {

    private AtomicBoolean isCommitted=new AtomicBoolean(false);
    private ResponseInfoAcess responseInfoAcess;
    private List<Cookie> cookies=new ArrayList<>();

    Charset responseCharacterEncoding;

    private void vetifyState(){
        if (!isCommitted.get()){
            throw new IllegalStateException("response has committed");
        }
    }

    private void committed(){
        if (isCommitted.compareAndSet(false,true)){
            String cookie=getCookiesStr();
            if (isVaildValue(cookie)){
                responseInfoAcess.setHeader(HttpHeadConstant.H_SET_COOKIE
                        , cookie);
            }
            responseInfoAcess.commit();
        }
    }

    private String getCookiesStr(){
        StringBuilder builder=new StringBuilder();

        String split=null;
        for (Cookie c:cookies){
            if (split==null){
                builder.append(cookieSerial(c));
                split=", ";
            }else {
                builder.append(split)
                        .append(cookieSerial(c));
            }
        }

        return builder.toString();
    }

    private String cookieSerial(Cookie cookie){
        String split="; ";
        StringBuilder builder=new StringBuilder();
        String name=cookie.getName();
        String vlaue=cookie.getValue();
        String domain=cookie.getDomain();
        String path=cookie.getPath();
        String comment=cookie.getComment();
        boolean httponly=cookie.isHttpOnly();
        int maxage=cookie.getMaxAge();
        boolean secure=cookie.getSecure();

        //base
        builder.append(name)
                .append("=")
                .append(vlaue);

        if (maxage!=-1&&maxage>0){
            long cur=System.currentTimeMillis();
            cur+=(maxage*1000);
            Date date=Date.from(Instant.ofEpochMilli(cur));
            DateFormat format=DateFormat.getDateTimeInstance(0,0,Locale.ENGLISH);
            builder.append(split)
                    .append("expires=")
                    .append(date.toString());
            System.out.println(format.format(date));
        }

        if (isVaildValue(path)){
            builder.append(split)
                    .append("path=")
                    .append(path);
        }

        if (isVaildValue(domain)){
            builder.append(split)
                    .append("domain=")
                    .append(domain);
        }

        if (httponly){
            builder.append(split)
                    .append("HttpOnly");
        }


        if (secure){
            builder.append(split)
                    .append("Secure");
        }

        if (isVaildValue(comment)){
            builder.append(split)
                    .append(comment);
        }

        return builder.toString();
    }

    private boolean isVaildValue(String val){
        return val!=null&&!val.equals("");
    }


    MappedServletResponse(ResponseInfoAcess responseInfoAcess) {
        this.responseInfoAcess = responseInfoAcess;
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (!isCommitted.get()){
            cookies.add(cookie);
        }
    }

    @Override
    public boolean containsHeader(String name) {
        return responseInfoAcess.getHeader(name)!=null;
    }

    //todo 一下未实现
    @Override
    public String encodeURL(String url) {
        return null;
    }

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

    //todo 以上未实现

    @Override
    public void sendError(int sc, String msg) throws IOException {
        sendError(sc);
    }

    @Override
    public void sendError(int sc) throws IOException {
        setStatus(sc);
        committed();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        setStatus(HttpServletResponse.SC_SEE_OTHER);
        addHeader(HttpHeadConstant.H_LOC,location);
        committed();
    }

    @Override
    public void setDateHeader(String name, long date) {
        Date realDate=Date.from(Instant.ofEpochSecond(date));
        setHeader(name,realDate.toString());
    }

    @Override
    public void addDateHeader(String name, long date) {
        Date realDate=Date.from(Instant.ofEpochSecond(date));
        addHeader(name,realDate.toString());
    }

    @Override
    public void setHeader(String name, String value) {
        responseInfoAcess.setHeader(name,value);
    }

    @Override
    public void addHeader(String name, String value) {
        responseInfoAcess.addHead(name,value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        String sval=value+"";
        setHeader(name,sval);
    }

    @Override
    public void addIntHeader(String name, int value) {
        String sval=value+"";
        addHeader(name,sval);
    }

    @Override
    public void setStatus(int sc) {
        responseInfoAcess.setState(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        setStatus(sc);
    }

    @Override
    public int getStatus() {
        return responseInfoAcess.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return responseInfoAcess.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return responseInfoAcess.getHeaderNames();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return responseInfoAcess.getHeaderNames();
    }

    @Override
    public String getCharacterEncoding() {
        return responseCharacterEncoding.displayName();
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeadConstant.H_CONT_TYPE);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (isCommitted.get()){
            throw new IOException("can't open outputstream");
        }
        return responseInfoAcess.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (isCommitted.get()){
            throw new IOException("can't open outputstream");
        }
        return responseInfoAcess.getWriter();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        responseCharacterEncoding=Charset.forName(charset);
    }

    @Override
    public void setContentLength(int len) {
        setIntHeader(HttpHeadConstant.H_CONT_LEN,len);
    }

    @Override
    public void setContentLengthLong(long len) {
        throw new UnsupportedOperationException("" +
                "the content len is less than Integer.MaxValue");
    }

    @Override
    public void setContentType(String type) {
        setHeader(HttpHeadConstant.H_CONT_TYPE,type);
    }

    @Override
    public void setBufferSize(int size) {
        //不打算使用这个函数
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        committed();
    }

    @Override
    public void resetBuffer() {
        responseInfoAcess.clearData();
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {
        responseInfoAcess.clearHears();
        responseInfoAcess.clearData();
        responseInfoAcess.setState(HttpServletResponse.SC_OK);
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException("setLocale(Locale loc)");
    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
