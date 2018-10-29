package com.sicnu.cs.http;

import java.util.HashMap;

public class HttpRequest {

    private int contentLength=0;
     HashMap<String,String> headers=new HashMap<>();
    private String HTTPVersion;
    private String method;
    private String url;
    private byte[] data;


    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setHeader(String name,String val){
        headers.put(name,val);
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public String getHTTPVersion() {
        return HTTPVersion;
    }

    public void setHTTPVersion(String HTTPVersion) {
        this.HTTPVersion = HTTPVersion;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
