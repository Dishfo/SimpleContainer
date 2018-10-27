package com.cs.sicnu.http;

import com.cs.sicnu.core.protocol.HttpResponse;
import com.cs.sicnu.core.protocol.StatusConstant;
import com.cs.sicnu.core.utils.BytesServletOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;

public class HttpResponseWrapper implements HttpServletResponse {

    private HttpResponse response;
    private Context context;
    private ByteBuffer buffer;
    private HashMap<String,String> headers;
    private String contentType;
    private long contentLength;
    private int status=200;
    private String characterEncoding=null;
    private volatile boolean iscommitted=false;
    private BytesServletOutputStream outputStream;
    private PrintWriter printWriter;

    public static final  int MAX_BUFF_SIZE=1024*1024*8;
    private HttpRequestWrapper requestWrapper;

    public HttpResponseWrapper(Context context) {
        this.context = context;
        buffer=ByteBuffer.allocate(MAX_BUFF_SIZE);
        headers=new HashMap<>();
        outputStream=new BytesServletOutputStream(MAX_BUFF_SIZE);
        printWriter=new PrintWriter(outputStream);
    }

    public HttpRequestWrapper getRequestWrapper() {
        return requestWrapper;
    }

    public void setRequestWrapper(HttpRequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("addCookie");
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
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
        setStatus(sc);
    }

    @Override
    public void sendError(int sc) throws IOException {
        setStatus(sc);
        commit();
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException("sendRedirect");
    }

    @Override
    public void setDateHeader(String name, long date) {
        Date date1=Date.from(Instant.ofEpochMilli(date));
        headers.put(name,date1.toString());
    }

    @Override
    public void addDateHeader(String name, long date) {
        Date date2=Date.from(Instant.ofEpochMilli(date));
        headers.merge(name,date2.toString(),(a,b)-> a+";"+b);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name,value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.merge(name, value, (a, b) -> a + ";" + b);
    }

    @Override
    public void setIntHeader(String name, int value) {
        headers.put(name,Integer.toString(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        headers.merge(name,Integer.toString(value),(a,b)->a+";"+b);
    }

    @Override
    public void setStatus(int sc) {
        this.status=sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        throw new UnsupportedOperationException("method setStatus(int,string) has been deprecated");
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        String value=headers.get(name);
        if (value==null){
            return null;
        }else {
            return Arrays.asList(value.split(","));
        }
    }

    public HttpResponse getResponse() {
        return response;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        if (characterEncoding!=null){
            return characterEncoding;
        }else {
            return context.getResponseCharacterEncoding();
        }
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding=charset;
    }

    @Override
    public void setContentLength(int len) {
        if (len<0||len>MAX_BUFF_SIZE){
            throw new IllegalArgumentException("size must more than zero and less than MAX_BUFF_SIZE");
        }
        contentLength=len;
        setHeader("Content-Length",Integer.toString(len));
    }

    @Override
    public void setContentLengthLong(long len) {
        if (len<0||len>MAX_BUFF_SIZE){
            throw new IllegalArgumentException("size must more than zero and less than MAX_BUFF_SIZE");
        }
        contentLength=len;
        setHeader("Content-Length",Long.toString(len));
    }

    @Override
    public void setContentType(String type) {
        this.contentType=type;
        headers.put("content-Type",type);
    }

    @Override
    public void setBufferSize(int size) {
        if (size<0||size>MAX_BUFF_SIZE){
            throw new IllegalArgumentException("size must more than zero and less than MAX_BUFF_SIZE");
        }
        buffer=ByteBuffer.allocate(size);
    }

    @Override
    public int getBufferSize() {
        return buffer.capacity();
    }

    @Override
    public void flushBuffer() throws IOException {
        commit();
        buffer.clear();
    }

    private HttpResponse commitToResponse(){
        HttpResponse.Builder builder=new HttpResponse.Builder();
        return builder.build();
    }

    /**
     * the method may make the response
     * be committed
     *
     */
    protected void commit(){
        if (iscommitted){
            throw new IllegalStateException("response has commit");
        }
        printWriter.flush();
        HttpResponse.Builder builder=new HttpResponse.Builder();
        byte[] data=outputStream.toByteArray();
        builder.setData(ByteBuffer.wrap(data,0,data.length))
                .setMsg(StatusConstant.getMsg(getStatus()))
                .setStatus(status)
                .setVersion(requestWrapper.getProtocol());
        setContentLength(data.length);
        String type=getContentType();
        builder.setHead("Content-type",type==null?"text/plain":type);

        headers.forEach(builder::setHead);

        response=builder.build();
        try {
            response.setLen(Integer.valueOf(getHeader("Content-Length")));
        }catch (Throwable t){
            response.setLen(0);
        }
        iscommitted=true;
    }

    @Override
    public void resetBuffer() {
        if (!iscommitted){
            buffer.clear();
        }else {
            throw new IllegalStateException("response has commit");
        }
    }

    private void stateVertify(){
        if (iscommitted){
            throw new IllegalStateException("response has committed");
        }
    }

    @Override
    public boolean isCommitted() {
        return iscommitted;
    }

    @Override
    public void reset() {
        if (iscommitted){
            throw new IllegalStateException("reset after the response has commited");
        }
        buffer.clear();
        headers.clear();
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException("setLoacle");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("getLoacle");
    }
}
