package com.sicnu.cs.servlet.http;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.sicnu.cs.servlet.basis.ByteServletOutputStream;
import com.sicnu.cs.servlet.basis.HttpPair;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class InteralHttpServletResponse implements HttpServletResponse {

    private HttpPair pair;
    private ServletContext context;
    private String characterEncoding;

    public InteralHttpServletResponse(HttpPair pair,
                                      ServletContext context) {
        this.pair = pair;
        this.context = context;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return pair.getRequsetHead(name)!=null;
    }

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

    @Override
    public void sendError(int sc, String msg) throws IOException {
        sendError(sc);
    }

    @Override
    public void sendError(int sc) throws IOException {
        pair.setStatus(sc);
        pair.commitResponse();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        pair.setStatus(303);
        pair.setResponseHead(HttpHeadConstant.H_LOC,location);
        pair.commitResponse();
    }

    @Override
    public void setDateHeader(String name, long date) {
        Date d=Date.from(Instant.ofEpochMilli(date));
        setHeader(name,d.toString());
    }

    @Override
    public void addDateHeader(String name, long date) {
        Date d=Date.from(Instant.ofEpochMilli(date));
        addHeader(name,d.toString());
    }

    @Override
    public void setHeader(String name, String value) {
        pair.setResponseHead(name,value);
    }

    @Override
    public void addHeader(String name, String value) {
        pair.addResponseHead(name,value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name,Integer.toString(value));

    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name,Integer.toString(value));
    }

    @Override
    public void setStatus(int sc) {
        pair.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        pair.setStatus(sc);
    }

    @Override
    public int getStatus() {
        return pair.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return pair.getResponseHead(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        if (characterEncoding==null){
            characterEncoding=context.getResponseCharacterEncoding();
        }
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeadConstant.H_CONT_TYPE);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ByteServletOutputStream(pair.getOutPutStream());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        Writer writer=pair.getWriter();
        return new PrintWriter(writer);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        Charset.forName(charset);
        this.characterEncoding=charset;
    }

    @Override
    public void setContentLength(int len) {
        setHeader(HttpHeadConstant.H_CONT_LEN,len+"");
    }

    @Override
    public void setContentLengthLong(long len) {
        setHeader(HttpHeadConstant.H_CONT_LEN,len+"");
    }

    @Override
    public void setContentType(String type) {
        setHeader(HttpHeadConstant.H_CONT_TYPE,type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 1024*1024*8;
    }

    @Override
    public void flushBuffer() throws IOException {
        pair.commitResponse();
    }

    @Override
    public void resetBuffer() {
        pair.clearBody();
    }

    @Override
    public boolean isCommitted() {
        return pair.isCommitted();
    }

    @Override
    public void reset() {
        pair.clearHeader();
        pair.clearBody();
    }


    @Override
    public void setLocale(Locale loc) {
        setHeader(HttpHeadConstant.H_CONT_LANG,loc.toString());
    }

    @Override
    public Locale getLocale() {
        String val=getHeader(HttpHeadConstant.H_CONT_LANG);
        if (val==null){
            return Locale.CHINA;
        }else {
            String[] emp=val.split("_");
            if (emp.length!=2){
                return Locale.CHINA;
            }else {
                return new Locale(emp[0],emp[1]);
            }
        }
    }
}
