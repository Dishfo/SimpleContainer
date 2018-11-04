package com.sicnu.cs.servlet.basis;

import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.http.HttpRequest;
import com.sicnu.cs.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 对 httpConnnection httpRequset httpResponse
 * 的访问接口
 */
public class HttpPair {

    private HttpConnection connection;
    private HttpResponse response;
    private HttpRequest request;
    private URI uri=null;
    private AtomicBoolean committed;

    public HttpPair(HttpConnection connection,
                    HttpResponse response,
                    HttpRequest request) {
        this.connection = connection;
        this.response = response;
        this.request = request;
        this.committed=new AtomicBoolean(false);
    }

    public String getSchema(){
        return connection.getSchema();
    }

    public InetSocketAddress getRemote(){
        return connection.getRemote();
    }

    public InetSocketAddress getLocalHost(){
        return connection.getHost();
    }

    public String getRequsetHead(String name){
        return request.getHeader(name);
    }

    public void setResponseHead(String name,String val){
        response.setHeader(name.toLowerCase()
                ,val);
    }

    public void addResponseHead(String name,String val){
        response.addHeader(name.toLowerCase()
                ,val);
    }

    public String getResponseHead(String name){ return response.getHead(name);}

    public URI getRequsetUrl(){
        if (uri==null){
            uri=URI.create(request.getUrl());
        }
        return uri;
    }

    public byte[] getInputData(){
        return request.getData();
    }

    public int getStatus(){
        return response.getStatus();
    }

    public String getMethod(){
        return request.getMethod();
    }

    public String getRequsetVersion(){
        return request.getHTTPVersion();
    }

    public void setResponseVersion(String version){
        response.setVersion(version);
    }

    public void setStatus(int sc){
        response.setStatus(sc);
    }

    public Map<String,String> getRequsetHeaders(){
        return Collections.unmodifiableMap(request.getHeaders());
    }

    public void setHeadCharset(String charset){
        response.setHeadEncoding(charset);
    }

    public OutputStream getOutPutStream() throws IOException {
        return response.getBodyOutStream();
    }

    public Writer getWriter() throws IOException {
        return response.getBodyWriter();
    }

    public void clearHeader(){
        response.cleanHead();
    }

    public int getBufferSize(){
        return  1024*1024*8;
    }

    public void clearBody(){
        response.cleanBody();
    }

    public void commitResponse()throws IOException{
        if (committed.compareAndSet(false,true)){

            response.outPut();
        }
    }

    public boolean isCommitted(){
        return committed.get();
    }
}
