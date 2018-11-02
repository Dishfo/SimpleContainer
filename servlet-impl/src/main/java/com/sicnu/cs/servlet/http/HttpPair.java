package com.sicnu.cs.servlet.http;

import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.http.HttpRequest;
import com.sicnu.cs.http.HttpResponse;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 对 httpConnnection httpRequset httpResponse
 * 的访问接口
 */
public class HttpPair {

    private HttpConnection connection;
    private HttpResponse response;
    private HttpRequest request;

    public HttpPair(HttpConnection connection,
                    HttpResponse response,
                    HttpRequest request) {
        this.connection = connection;
        this.response = response;
        this.request = request;
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
        response.setHeader(name,val);
    }

    public void addResponseHead(String name,String val){
        response.addHeader(name,val);
    }

    public String getRequsetUrl(){
        return request.getUrl();
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

    public void commitResponse()throws IOException{
        response.outPut();
    }
}
