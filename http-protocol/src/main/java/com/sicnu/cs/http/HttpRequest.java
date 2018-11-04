package com.sicnu.cs.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private int contentLength=0;
     HashMap<String,String> headers=new HashMap<>();
    private String HTTPVersion;
    private String method;
    private String url;
    private byte[] data;


    int getContentLength() {
        return contentLength;
    }

    void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    void setHeader(String name, String val){
        headers.put(name,val);
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public String getHTTPVersion() {
        return HTTPVersion;
    }

    void setHTTPVersion(String HTTPVersion) {
        this.HTTPVersion = HTTPVersion;
    }

    public String getMethod() {
        return method;
    }

    void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    public byte[] getData() {
        return data;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    void setData(byte[] data) {
        this.data = data;
    }
}
